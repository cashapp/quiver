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
