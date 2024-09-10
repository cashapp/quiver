package app.cash.quiver.extensions

import app.cash.quiver.Failure
import app.cash.quiver.Outcome
import app.cash.quiver.Present
import app.cash.quiver.asOutcome
import app.cash.quiver.toOutcome
import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.left
import arrow.core.right

/**
 * Transforms a `Result<T>` into an `ErrorOr<T>`
 */
fun <T> Result<T>.toEither(): ErrorOr<T> = this.map { Either.Right(it) }.getOrElse { Either.Left(it) }

/**
 * Transforms a `Result<T>` into an `OutcomeOf<T>`
 */
fun <T> Result<T>.toOutcome(): OutcomeOf<T> = this.map { Either.Right(it) }.getOrElse { Either.Left(it) }.asOutcome()

/**
 * Transforms a `Result<Option<T>>` to an `OutcomeOf<A>`
 */
fun <T> Result<Option<T>>.toOutcomeOf(): OutcomeOf<T> = this.toEither().toOutcome()


/**
 * Make anything a Success.
 */
fun <T> T.success(): Result<T> = Result.success(this)

/**
 * Make any exception a Failure.
 */
fun <A> Throwable.failure(): Result<A> = Result.failure(this)

/**
 * Turns a nullable value into a [Result].
 */
inline fun <A : Throwable, B> B?.toResult(error: () -> A): Result<B> =
  this?.let { Result.success(it) } ?: Result.failure(error())

/**
 * Turns an [Option] value into a [Result].
 */

inline fun <A, B: Throwable> Option<A>.toResult(error: () -> B): Result<A> = this.getOrNull().toResult(error)

/**
 * If a [Result] is a failure, maps the underlying [Throwable] to a new [Throwable].
 */
inline fun <N : Throwable, T> Result<T>.mapFailure(
  f: (exception: Throwable) -> N
): Result<T> {
  return when (val exception = exceptionOrNull()) {
    null -> Result.success(getOrThrow())
    else -> Result.failure(f(exception))
  }
}

/**
 * Calls the specified function block and returns its encapsulated result if invocation was successful, catching any
 * non-fatal exceptions thrown from the block function execution and encapsulating them as failures.
 */
@JvmName("tryCatch")
inline fun <R> Result.Companion.catch(f: () -> R): Result<R> =
  arrow.core.raise.catch({ success(f()) }) { failure(it) }

/**
 * Retrieves the success of a Result, or throws the failure. Alias of `getOrThrow`, included for consistency with ErrorOr.
 */
fun <A> Result<A>.orThrow() = getOrThrow()

/**
 * Flattens a `Result<Result<T>>` into a `Result<T>`
 */
fun <T> Result<Result<T>>.flatten(): Result<T> = flatMap(::identity)

/**
 * Map success to Unit, included for consistency with Either.
 */
fun <T> Result<T>.unit() = map { }

/**
 * Performs an effect over successes but maps the original value back into
 * the Result.
 */
inline fun <A, B> Result<A>.tap(f: (A) -> B): Result<A> = this.map { a ->
  f(a)
  a
}

/**
 * Performs an effect over successes but maps the original value back into
 * the Result.  This is useful for mixing with validation functions.
 */
inline fun <A> Result<A>.flatTap(f: (A) -> Result<Any>): Result<A> = this.flatMap { a ->
  f(a).map { a }
}

/**
 * Returns false if Success or returns the result of the given predicate to the Failure value.
 */
inline fun <T> Result<T>.isFailure(predicate: (Throwable) -> Boolean): Boolean =
  fold(onFailure = predicate, onSuccess = { false })

/**
 * Returns false if Failure or returns the result of the given predicate to the Success value.
 */
inline fun <T> Result<T>.isSuccess(predicate: (T) -> Boolean): Boolean =
  fold(onFailure = { false }, onSuccess = predicate)

/**
 * Recovers errors with a function from Throwable to Result.
 */
inline fun <T> Result<T>.handleFailureWith(f: (Throwable) -> Result<T>): Result<T> = recoverCatching {
  f(it).getOrThrow()
}
