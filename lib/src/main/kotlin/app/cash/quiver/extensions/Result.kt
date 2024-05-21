package app.cash.quiver.extensions

import arrow.core.Either

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
fun <T : Throwable> T.failure(): Result<T> = Result.failure(this)


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
