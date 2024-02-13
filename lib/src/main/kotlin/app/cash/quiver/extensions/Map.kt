package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrNone
import arrow.core.right
import arrow.core.some
import kotlin.experimental.ExperimentalTypeInference
import app.cash.quiver.extensions.traverse as quiverTraverse

/**
 * Extension function to get an Option from a nullable object on a map.
 */
fun <K, A> Map<K, A>.getOption(k: K): Option<A> =
  getOrNone(k)

/**
 * Map a function that returns an Either over all the values in the Map.
 * If the function returns a Right, the value is added to the resulting Map.
 * If the function returns a Left, the result is the Left.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <K, E, A, B> Map<K, A>.traverse(f: (A) -> Either<E, B>): Either<E, Map<K, B>> {
  val acc = mutableMapOf<K, B>()
  forEach { (k, v) ->
    when (val res = f(v)) {
      is Either.Right -> acc[k] = res.value
      is Either.Left -> return@traverse res
    }
  }
  return acc.right()
}

/**
 * Synonym for traverse((A)-> Either<E, B>): Either<E, Map<K, B>>
 */
inline fun <K, E, A, B> Map<K, A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Map<K, B>> =
  quiverTraverse(f)

/**
 * Map a function that returns an Option over all the values in the Map.
 * If the function returns a Some, the value is added to the resulting Map.
 * If the function returns a None, the result is None.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <K, A, B> Map<K, A>.traverse(f: (A) -> Option<B>): Option<Map<K, B>> {
  val acc = mutableMapOf<K, B>()
  forEach { (k, v) ->
    when (val res = f(v)) {
      is Some -> acc[k] = res.value
      is None -> return@traverse res
    }
  }
  return acc.some()
}

/**
 * Synonym for traverse((A)-> Option<B>): Option<Map<K, B>>
 */
inline fun <K, A, B> Map<K, A>.traverseOption(f: (A) -> Option<B>): Option<Map<K, B>> =
  quiverTraverse(f)
