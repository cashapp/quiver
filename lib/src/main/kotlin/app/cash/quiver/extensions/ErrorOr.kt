package app.cash.quiver.extensions

import arrow.core.Either

typealias ErrorOr<T> = Either<Throwable, T>
