package app.cash.quiver.extensions

import arrow.core.Either
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
