@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DEPRECATION")

package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.ValidatedNel
import arrow.core.getOrElse
import arrow.core.nonEmptyListOf
import arrow.core.raise.option
import arrow.core.right

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
infix fun <T> Option<T>.or(other: Option<T>): Option<T> = when (this) {
  is Some -> this
  is None -> other
}

/**
 * Given a function that returns an Either, will turn your Option of A into an Option of B in the context of Either,
 * where the Left value will always be the None.
 */
inline fun <E, A, B> Option<A>.traverseOp(fa: (A) -> Either<E, B>): Either<E, Option<B>> = fold(
  { None.right() },
  { fa(it).map(::Some) }
)

/**
 * Given an optional value A and B, will return you an optional C
 */
inline fun <A, B, C> Option<A>.zipOp(b: Option<B>, f: (A, B) -> C): Option<C> = option {
  f(bind(), b.bind())
}

/**
 * Will return an empty string if the Option supplied is None
 */
fun <T> Option<T>.orEmpty(f: (T) -> String): String = this.map(f).getOrElse { "" }

