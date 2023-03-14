package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.flatten
import arrow.core.identity
import arrow.fx.coroutines.Schedule
import java.time.Duration

/**
 * Map on this suspended supplier with a suspended function.
 */
fun <A, B> (suspend () -> A).map(f: (A) -> B): suspend () -> B = suspend {
  f(this.invoke())
}

/**
 * Map on this suspended function with another.
 */
fun <A, B, C> (suspend (A) -> B).map(f: (B) -> C): suspend (A) -> C = { a: A ->
  f(this.invoke(a))
}

/**
 * Retry a suspended supplier until a maximum number of times or a predicate has been fulfilled.
 *
 * @param until the predicate that indicates success. Defaults to "no exceptions encountered".
 * @param additionalTimes how many times to retry before giving up. Defaults to 4 (5 attempts in total).
 * @param delay how long to delay between attempts. Defaults to 20ms.
 * @param exponentialBackoff if true, will double the delay on each attempt. Defaults to false.
 * @param jitter if true, will stagger the attempts to reduce likelihood of collisions. Defaults to false.
 */
suspend fun <T> (suspend () -> T).withRetriesOrThrow(
  until: (ErrorOr<T>) -> Boolean = { it.isRight() },
  additionalTimes: Int = 4,
  delay: Duration = Duration.ofMillis(20L),
  exponentialBackoff: Boolean = false,
  jitter: Boolean = false,
): ErrorOr<T> = withRetries(until, additionalTimes, delay, exponentialBackoff, jitter) {
  Either.catch { invoke() }
}

/**
 * Retry a suspended supplier of an ErrorOr until a maximum number of times or a predicate has been fulfilled.
 *
 * @param until the predicate that indicates success. Defaults to "no exceptions encountered".
 * @param additionalTimes how many times to retry before giving up. Defaults to 4 (5 attempts in total).
 * @param delay how long to delay between attempts. Defaults to 20ms.
 * @param exponentialBackoff if true, will double the delay on each attempt. Defaults to false.
 * @param jitter if true, will stagger the attempts to reduce likelihood of collisions. Defaults to false.
 */
suspend fun <T> (suspend () -> ErrorOr<T>).withRetries(
  until: (ErrorOr<T>) -> Boolean = { it.isRight() },
  additionalTimes: Int = 4,
  delay: Duration = Duration.ofMillis(20L),
  exponentialBackoff: Boolean = false,
  jitter: Boolean = false,
): ErrorOr<T> = withRetries(until, additionalTimes, delay, exponentialBackoff, jitter) {
  invoke()
}

private suspend fun <T> withRetries(
  until: (ErrorOr<T>) -> Boolean,
  additionalTimes: Int,
  delay: Duration,
  exponentialBackoff: Boolean,
  jitter: Boolean,
  f: suspend () -> ErrorOr<T>,
): ErrorOr<T> {
  val baseSchedule =
    if (exponentialBackoff) Schedule.exponential(delay.toNanos().toDouble())
    else Schedule.spaced<ErrorOr<T>>(delay.toNanos().toDouble())
  val schedule = baseSchedule
    .untilInput<ErrorOr<T>> { until(it) }
    .and(Schedule.recurs(additionalTimes))
    .zipRight(Schedule.identity()).let {
      if (jitter) it.jittered() else it
    }
  return schedule.repeat { f() }
}
