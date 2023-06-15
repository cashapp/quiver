package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.raise.either

/**
 * Returns an Either of a list of B results of applying the given transform function
 * to each element(A) in the original collection.
 */
inline fun <E, A, B> Iterable<A>.traverse(f: (A) -> Either<E, B>): Either<E, Iterable<B>> =
  let { l -> either { l.map { f(it).bind() } } }
