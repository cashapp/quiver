@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DEPRECATION")

package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.ValidatedNel
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.invalidNel
import arrow.core.left
import arrow.core.right
import arrow.core.validNel
import arrow.core.zip

/**
 * Turns your Validated List into an Either, but will throw an exception in the Left hand case.
 */
fun <E, A> ValidatedNel<E, A>.attemptValidated(): Either<Throwable, A> =
  this.toEither()
    .mapLeft { errors -> RuntimeException(errors.toString()) }

/**
 * Given a predicate and an error generating function return either the original value in a ValidNel if the
 * predicate evaluates as true or the generated error in an InvalidNel.
 *
 * eg:
 * "hi mum".validate({it.contains("hi")},{"where's your manners?"})
 *
 */
inline fun <ERR, A> A.validate(predicate: (A) -> Boolean, error: (A) -> ERR): ValidatedNel<ERR, A> =
  if (predicate(this)) this.validNel() else error(this).invalidNel()

/**
 * Given a predicate and an error generating function return either the original value in a Right if the
 * predicate evaluates as true or the error as a Left.
 *
 * eg:
 * "hi mum".validate({it.contains("hi")},{"where's your manners?"})
 *
 */
inline fun <ERR, A> A.validateEither(predicate: (A) -> Boolean, error: (A) -> ERR): Either<ERR, A> =
  if (predicate(this)) this.right() else error(this).left()

/**
 * Often you have two validations that return the same thing, and you don't want necessarily
 * to pair them. takeLeft will return the value of the left side iff both validations
 * succeed.
 *
 * eg.
 *
 * Valid("hi").takeLeft(Valid("mum")) == Valid("hi")
 */
fun <ERR, A> ValidatedNel<ERR, A>.takeLeft(other: ValidatedNel<ERR, A>): ValidatedNel<ERR, A> =
  this.zip(other) { a, _ ->
    a
  }

/**
 * Often you have two validations that return the same thing, and you don't want necessarily
 * to pair them. takeRight will return the value of the right side iff both validations
 * succeed.
 *
 * eg.
 *
 * Valid("hi").takeRight(Valid("mum")) == Valid("mum")
 */
fun <ERR, A> ValidatedNel<ERR, A>.takeRight(other: ValidatedNel<ERR, A>): ValidatedNel<ERR, A> =
  this.zip(other) { _, b ->
    b
  }

/**
 * Given a mapping function and an error message, return either the result of the function in a
 * ValidNel if the function completes successfully, or the error message in an InvalidNel.
 */
inline fun <ERR, A, B> A.validateMap(
  f: (A) -> Either<Throwable, B>,
  error: (A, Throwable) -> ERR
): ValidatedNel<ERR, B> =
  f(this).map { it.validNel() }.getOrElse { error(this, it).invalidNel() }

/**
 * The Validated type doesn't natively support flatMap because of the monad laws that it breaks. But this
 * is what flatMap would do if it were allowed. We've called it concatMap because the Kotlin compiler will
 * want to wire in the Monad flatMap extension instead and confusion reigns.
 *
 * eg:
 *
 * val maybeParty: ValidatedNel<String, Party> = ...
 *
 * val result : ValidatedNel<String,String> = maybeParty.concatMap { party -> validateCashTag(party.responder.cashTag) }
 *
 * result == ValidNel("$jackjack")
 */
inline fun <ERR, A, B> ValidatedNel<ERR, A>.concatMap(f: (A) -> ValidatedNel<ERR, B>): ValidatedNel<ERR, B> =
  this.withEither { either -> either.flatMap { f(it).toEither() } }
