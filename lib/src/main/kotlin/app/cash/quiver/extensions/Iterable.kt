package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.either
import kotlin.experimental.ExperimentalTypeInference

/**
 * Returns an Either of a list of B results of applying the given transform function
 * to each element(A) in the original collection.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <E, A, B> Iterable<A>.traverse(f: (A) -> Either<E, B>): Either<E, List<B>> =
  let { l -> either { l.map { f(it).bind() } } }

/**
 * Synonym for traverse((A)-> Either<E, B>): Either<E, List<B>>
 */
inline fun <E, A, B> Iterable<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, List<B>> =
  traverse(f)

/**
 * Returns an Option of a list of B results of applying the given transform function
 * to each element(A) in the original collection.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <A, B> Iterable<A>.traverse(f: (A) -> Option<B>): Option<List<B>> {
  val result = ArrayList<B>()
  for (element in this) {
    val mapped = f(element)
    if (mapped is Some) result.add(mapped.value)
    else return None
  }
  return Some(result)
}

/**
 * Synonym for traverse((A)-> Option<B>): Option<List<B>>
 */
inline fun <A, B> Iterable<A>.traverseOption(f: (A) -> Option<B>): Option<List<B>> =
  traverse(f)

