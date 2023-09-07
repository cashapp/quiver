package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.raise.either
import arrow.core.raise.option
import arrow.core.toNonEmptyListOrNull
import arrow.core.toOption
import kotlin.experimental.ExperimentalTypeInference

/**
 * Applies a function that produces an Option to a NonEmptyList.
 * The result is None if the resulting list would be empty, otherwise Some(NonEmptyList).
 */
inline fun <A, B> Nel<A>.mapNotNone(f: (A) -> Option<B>): Option<Nel<B>> =
  this.toList().flatMap { a -> f(a).toList() }.toNonEmptyListOrNull().toOption()

/**
 * Map a function that returns an Either across the NonEmptyList.
 *
 * The first Left result from calling the function will be the result, or if no calls result in a Left
 * the result will be a Right(NonEmptyList) of all the B's returned.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <E, A, B> Nel<A>.traverse(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> =
  let { nel -> either { nel.map { f(it).bind() } } }

/**
 * Synonym for traverse((A)-> Either<E, B>): Either<E, NonEmptyList<B>>
 */
inline fun <E, A, B> Nel<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, NonEmptyList<B>> =
  traverse(f)

/**
 * Map a function that returns an Option across the NonEmptyList.
 *
 * The first None result from calling the function will be the result, or if no calls result in a None
 * the result will be a Some(NonEmptyList) of all the B's returned.
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <A, B> NonEmptyList<A>.traverse(f: (A) -> Option<B>): Option<NonEmptyList<B>> =
  let { nel -> option { nel.map { f(it).bind() } } }

/**
 * Synonym for traverse((A)-> Option<B>): Option<NonEmptyList<B>>
 */
inline fun <A, B> NonEmptyList<A>.traverseOption(f: (A) -> Option<B>): Option<NonEmptyList<B>> =
  traverse(f)

