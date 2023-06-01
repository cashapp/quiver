package app.cash.quiver.extensions

import arrow.core.Either

/**
 * Transforms a `Result<T>` into an `ErrorOr<T>`
 */
fun <T> Result<T>.toEither(): ErrorOr<T> = this.map { Either.Right(it) }.getOrElse { Either.Left(it) }
