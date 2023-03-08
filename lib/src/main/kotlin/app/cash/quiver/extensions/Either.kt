package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.toOption

/**
 * Retrieves the Right hand of an Either, or throws the Left hand error
 */
fun <A> Either<Throwable, A>.orThrow() = this.getOrElse { t -> throw t }

/**
 * Returns the first successful either, otherwise the last failure
 */
inline fun <E, A> Either<E, A>.or(f: () -> Either<E, A>): Either<E, A> = when (this) {
  is Either.Left -> f()
  is Either.Right -> this
}

/**
 * Turns your Either into an Option.
 */
fun <E, A> Either<E, A>.asOption(): Option<A> = when (this) {
  is Either.Left -> None
  is Either.Right -> Some(this.value)
}

/**
 * Turns the left side of your Either into an Option.
 */
fun <E, A> Either<E, A>.leftAsOption(): Option<E> = when (this) {
  is Either.Left -> Some(this.value)
  is Either.Right -> None
}

/**
 * Pass the left value to the function (e.g. for logging errors)
 */
suspend fun <A, B> Either<A, B>.tapLeft(f: suspend (A) -> Unit): Either<A, B> = this.mapLeft {
  f(it)
  it
}

/**
 * Performs an effect on the right side of the Either.
 */
suspend fun <A, B> Either<A, B>.forEach(f: suspend (B) -> Unit): Unit = when (this) {
  is Either.Left -> Unit
  is Either.Right -> f(this.value)
}

/**
 * Performs an effect on the left side of the Either.
 */
suspend fun <A, B> Either<A, B>.leftForEach(f: suspend (A) -> Unit): Unit = when (this) {
  is Either.Left -> f(this.value)
  is Either.Right -> Unit
}

/**
 * Performs an effect over the right side of the value but maps the original value back into
 * the Either.  This is useful for mixing with validation functions.
 */
inline fun <A, B, C> Either<A, B>.flatTap(f: (B) -> Either<A, C>): Either<A, B> = this.flatMap { b ->
  f(b).map { b }
}

/**
 * Lifts a nullable value into an Either, similar to toOption.  Must supply the left side
 * of the Either.
 */
fun <A, B> B?.toEither(left: () -> A): Either<A, B> = this.toOption().toEither { left() }

/**
 * Map on a nested Either Option type.
 */
fun <E, T, V> Either<E, Option<T>>.mapOption(f: (T) -> V): Either<E, Option<V>> = this.map { it.map(f) }
