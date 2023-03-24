package app.cash.quiver.extensions

import arrow.core.Option
import arrow.core.filterOption

/**
 * Returns a list without Nones
 */
fun <A> List<Option<A>>.filterNotNone(): List<A> = filterOption()

/**
 * Constructs a flattened list without Nones
 */
fun <A> listOfSome(vararg elements: Option<A>): List<A> = elements.toList().filterNotNone()

/**
 * Returns a list containing only the non-None results of applying the given transform function
 * to each element in the original collection.
 */
inline fun <A, B> List<A>.mapNotNone(f: (A) -> Option<B>): List<B> =
  this.flatMap { f(it).fold(::emptyList, ::listOf) }
