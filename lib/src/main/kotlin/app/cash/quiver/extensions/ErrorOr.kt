package app.cash.quiver.extensions

import arrow.core.Either

typealias ErrorOr<T> = Either<Throwable, T>

/**
 * Downgrades an ErrorOr to a Result.
 */
fun <T> ErrorOr<T>.toResult(): Result<T> = fold({ Result.failure(it) }, { Result.success(it) })
