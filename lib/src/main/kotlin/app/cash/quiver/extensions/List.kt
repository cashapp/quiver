package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.Option
import arrow.core.filterOption
import arrow.core.raise.either

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

/**
 * Returns an Either of a list of B results of applying the given transform function
 * to each element(A) in the original collection.
 */
inline fun <E, A, B> Iterable<A>.traverse(f: (A) -> Either<E, B>): Either<E, List<B>> =
  let { l -> either { l.map { f(it).bind() } } }

