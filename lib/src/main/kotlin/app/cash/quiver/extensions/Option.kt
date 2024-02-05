@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DEPRECATION")

package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.ValidatedNel
import arrow.core.getOrElse
import arrow.core.nonEmptyListOf
import kotlin.experimental.ExperimentalTypeInference
import app.cash.quiver.extensions.traverse as quiverTraverse

/**
 * Takes a function to run if your Option is None. Returns Unit if your Option is Some.
 */
inline fun <T> Option<T>.ifAbsent(f: () -> Unit): Unit {
  onNone { f() }
}

/**
 * Turns your Option into a Validated list of T if it's a Some.
 * If it's a None, will return a Nel of the error function passed in
 */
inline fun <T, E> Option<T>.toValidatedNel(error: () -> E): ValidatedNel<E, T> =
  ValidatedNel.fromOption(this) { nonEmptyListOf(error()) }

/**
 * Runs a side effect if the option is a Some
 */
inline fun <A> Option<A>.forEach(f: (A) -> Unit) {
  onSome { f(it) }
}

/**
 * Map some to Unit. This restores `.void()` which was deprecated by Arrow.
 */
fun <A> Option<A>.unit() = map { }

/**
 * Returns `this` if it's a Some, otherwise returns the `other` instance
 */
infix fun <T> Option<T>.or(other: () -> Option<T>): Option<T> = when (this) {
  is Some -> this
  is None -> other()
}

/**
 * Returns `this` if it's a Some, otherwise returns the `other` instance
 */
@Deprecated(
  "Use or(other: () -> Option<T>) instead",
  ReplaceWith("or { other }")
)
infix fun <T> Option<T>.or(other:  Option<T>): Option<T> = when (this) {
  is Some -> this
  is None -> other
}

/**
 * Will return an empty string if the Optional value supplied is None
 */
fun <T> Option<T>.orEmpty(f: (T) -> String): String = this.map(f).getOrElse { "" }

/**
 * Map a function that returns an Either across the Option.
 *
 * If the option is a None, return a Right(None).
 * If the option is a Some, apply f to the value. If f returns a Right, wrap the result in a Some.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <T, E, U> Option<T>.traverse(f: (T) -> Either<E, U>): Either<E, Option<U>> =
  fold( { Either.Right(None) }, { a -> f(a).map { Some(it) } })

/**
 * Synonym for traverse((T)-> Either<E, U>): Either<E, Option<U>>
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <T, E, U> Option<T>.traverseEither(f: (T) -> Either<E, U>): Either<E, Option<U>> =
  quiverTraverse(f)

/**
 * Map a function that returns an Iterable across the Option.
 *
 * If the option is a None, return an empty List
 * If the option is a Some, apply f to the value and wrap the result in a Some.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <A, B> Option<A>.traverse(f: (A) -> Iterable<B>): List<Option<B>> =
  fold( { emptyList() }, { a -> f(a).map { Some(it) } })
