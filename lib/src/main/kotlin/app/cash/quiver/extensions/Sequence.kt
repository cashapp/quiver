@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DEPRECATION")

package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlin.experimental.ExperimentalTypeInference
import app.cash.quiver.extensions.traverse as quiverTraverse

/**
 * Map a function that returns an Either across the Sequence.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, E, B> Sequence<A>.traverse(f: (A) -> Either<E, B>): Either<E, List<B>> {
  // Note: Using a mutable list here avoids the stackoverflows one can accidentally create when using
  //  Sequence.plus instead. But we don't convert the sequence to a list beforehand to avoid
  //  forcing too much of the sequence to be evaluated.
  val result = mutableListOf<B>()
  forEach { a ->
    when (val mapped = f(a)) {
      is Either.Right -> result.add(mapped.value)
      is Either.Left -> return@traverse mapped
    }
  }
  return Either.Right(result.toList())
}

/**
 * Synonym for traverse((A)-> Either<E, B>): Either<E, List<B>>
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, E, B> Sequence<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, List<B>> =
  quiverTraverse(f)

/**
 * Map a function that returns an Option across the Sequence.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <A, B> Sequence<A>.traverse(f: (A) -> Option<B>): Option<List<B>> {
  // Note: Using a mutable list here avoids the stackoverflows one can accidentally create when using
  //  Sequence.plus instead. But we don't convert the sequence to a list beforehand to avoid
  //  forcing too much of the sequence to be evaluated.
  val result = mutableListOf<B>()
  forEach { a ->
    when (val mapped = f(a)) {
      is Some -> result.add(mapped.value)
      is None -> return@traverse None
    }
  }
  return Some(result.toList())
}

/**
 * Synonym for traverse((A)-> Option<B>): Option<List<B>>
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B> Sequence<A>.traverseOption(f: (A) -> Option<B>): Option<List<B>> =
  quiverTraverse(f)
