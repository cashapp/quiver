package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.right
import arrow.core.toOption
import kotlin.experimental.ExperimentalTypeInference
import app.cash.quiver.extensions.traverse as quiverTraverse

/**
 * Retrieves the Right hand of an Either, or throws the Left hand error
 */
fun <A> Either<Throwable, A>.orThrow() = this.getOrElse { t -> throw t }

/**
 * Turns a nullable value into an [Either]. This is useful for building validation functions.
 *
 * @return [Either.Left] if the value is null, [Either.Right] if the value is not null.
 * @param label Optional [String] to identify the nullable value being evaluated, used in the failure message.
 */
fun <B> B?.validateNotNull(label: Option<String> = None): Either<Throwable, B> = this.toEither {
  IllegalArgumentException("Value${label.map { " (`$it`)" }.getOrElse { "" }} should not be null")
}

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
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B> Either<A, B>.tapLeft(f: (A) -> Unit): Either<A, B> = this.mapLeft {
  f(it)
  it
}

/**
 * Performs an effect on the right side of the Either.
 */
inline fun <A, B> Either<A, B>.forEach(f: (B) -> Unit): Unit = when (this) {
  is Either.Left -> Unit
  is Either.Right -> f(this.value)
}

/**
 * Performs an effect on the left side of the Either.
 */
inline fun <A, B> Either<A, B>.leftForEach(f: (A) -> Unit): Unit = when (this) {
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
inline fun <A, B> B?.toEither(left: () -> A): Either<A, B> = this.toOption().toEither { left() }

/**
 * Map on a nested Either Option type.
 */
inline fun <E, T, V> Either<E, Option<T>>.mapOption(f: (T) -> V): Either<E, Option<V>> = this.map { it.map(f) }

/**
 * Map right to Unit. This restores `.void()` which was deprecated by Arrow.
 */
fun <A, B> Either<A, B>.unit() = map { }

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <E, A, B> Either<E, A>.traverse(transform: (value: A) -> Iterable<B>): List<Either<E, B>> =
  when (this) {
    is Either.Left -> listOf(this)
    is Either.Right -> transform(value).map { it.right() }
  }

fun <E, A> Either<E, Iterable<A>>.sequence(): List<Either<E, A>> = quiverTraverse(::identity)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <E, A, B> Either<E, A>.traverse(transform: (value: A) -> Option<B>): Option<Either<E, B>> =
  when (this) {
    is Either.Left -> Some(this)
    is Either.Right -> transform(value).map { it.right() }
  }

fun <E, A> Either<E, Option<A>>.sequence(): Option<Either<E, A>> = quiverTraverse(::identity)
