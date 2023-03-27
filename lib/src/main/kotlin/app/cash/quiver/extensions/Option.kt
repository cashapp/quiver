package app.cash.quiver.extensions

import arrow.core.Option
import arrow.core.ValidatedNel
import arrow.core.nonEmptyListOf

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
