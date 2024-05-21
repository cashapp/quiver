@file:Suppress("DEPRECATION")

package app.cash.quiver

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.some
import app.cash.quiver.extensions.orThrow
import app.cash.quiver.raise.OutcomeRaise
import app.cash.quiver.raise.outcome
import arrow.core.raise.catch
import kotlin.experimental.ExperimentalTypeInference

/**
 * `Outcome` is a type that represents three possible states a result can be in: Present, Absent or Failure. Under the
 * hood it wraps the type `Either<E, Option<A>>` and supports the common functions that Eithers and Options support such
 * as [`map`](app.cash.quiver.Outcome.map), [`flatMap`](app.cash.quiver.Outcome.flatMap) and
 * [`zip`](app.cash.quiver.Outcome.zip).
 *
 * There are three primary constructors:
 *
 * ```kotlin
 * data class Present<A>(val value: A) : Outcome<Nothing, A>
 * data class Failure<E>(val error: E) : Outcome<E, Nothing>
 * object Absent : Outcome<Nothing, Nothing>
 * ```
 *
 * or you can use the extension methods thusly:
 *
 * ```kotlin
 * A.present()
 * E.failure()
 * ```
 *
 * You can also easily convert an `Either<Option<A>>` to an Outcome using  [`toOutcome()`](app.cash.quiver.toOutcome)
 *
 * ```kotlin
 * val outcome = "hi".some().right().toOutcome()
 * ```
 *
 * There is also a type alias `OutcomeOf<A>` which specialises the error side to a `Throwable` for your convenience.
 */
sealed class Outcome<out E, out A> constructor(val inner: Either<E, Option<A>>) {

  /**
   * Map safely transforms a value in the Outcome. It has no effect on `Absent` or `Failure` instances.
   *
   * ```kotlin
   * Present(1).map { it + 1 }     // Present(2)
   * Absent.map { it + 1 }         // Absent
   * Failure("bad").map { it + 1 } // Failure("bad")
   * ```
   */
  inline fun <B> map(f: (A) -> B): Outcome<E, B> = inner.map { it.map(f) }.toOutcome()

  /**
   * Performs an effect over the value and preserves the original `Outcome`
   *
   * ```kotlin
   * "hi".present().tap { println("$it world") } // Present("hi")
   * ```
   */
  inline fun <B> tap(f: (A) -> B): Outcome<E, A> = map { a -> f(a); a }

  fun isPresent(): Boolean = inner.fold({ false }) { it.isSome() }
  fun isAbsent(): Boolean = inner.fold({ false }) { it.isNone() }
  fun isFailure(): Boolean = inner.isLeft()

  companion object {
    /**
     * Catches any exceptions thrown by the function and lifts the result into an Outcome.  If your function
     * returns an option use `catchOption` instead
     */
    inline fun <R> catch(f: () -> R): Outcome<Throwable, R> =
      catch({ f().present() }) { it.failure() }

    /**
     * Catches any exceptions thrown by the function and lifts the result into an Outcome.  The Optional
     * value will be preserved as Present or Absent accordingly.
     *
     * Converts a function that throws an exception (throwable) and returns an Option into an Outcome
     *
     *
     * ```kotlin
     * val outcome: Outcome<Throwable, Customer> = Outcome.catchOption {
     *   val customer: Option<Customer> = maybeLoadCustomerOrThrow() // May or may not return a customer but throws on error
     *   customer
     * }
     * ```
     */
    inline fun <R> catchOption(f: () -> Option<R>): Outcome<Throwable, R> = Either.catch(f).toOutcome()
  }
}

/**
 * A data class representing the Presence of a value `A`.
 */
data class Present<A>(val value: A) : Outcome<Nothing, A>(value.some().right())
data class Failure<E>(val error: E) : Outcome<E, Nothing>(error.left())
object Absent : Outcome<Nothing, Nothing>(None.right())

fun <A> A.present(): Outcome<Nothing, A> = Present(this)
fun <E> E.failure(): Outcome<E, Nothing> = Failure(this)

/**
 * FlatMap allows multiple `Outcome`s to be safely chained together, passing the value from the previous as input into the
 * next function that produces an `Outcome`
 *
 * ```kotlin
 * fun <A, E, C> Outcome<E, A>.flatMap(f: (A) -> Outcome<E, C>): Outcome<E, C>
 * ```
 *
 * ```kotlin
 * Present(5).flatMap {
 *   if (it < 5) {
 *     Present(it)
 *   } else if (it < 10) {
 *     Absent
 *   } else {
 *     Failure("Value too high")
 *   }
 * }
 * ```
 */
inline fun <A, E, B> Outcome<E, A>.flatMap(f: (A) -> Outcome<E, B>): Outcome<E, B> =
  outcome { f(bind()).bind() }

inline fun <A, E> Outcome<E, A>.filter(p: (A) -> Boolean): Outcome<E, A> = outcome {
  bind().also { a -> ensure(p(a)) }
}

/**
 * Performs a flatMap across the supplied function, propagating failures or absence
 * but preserving the original present value.
 *
 *
 * ```kotlin
 * 1.present().flatTap { a -> "bad".failure() } // Failure("bad")
 * 1.present().flatTap { a -> Absent } // Absent
 * 1.present().flatTap { a -> a + 2 } // Present(1) -- value preserved
 * ```
 */
inline fun <A, E, B> Outcome<E, A>.flatTap(f: (A) -> Outcome<E, B>): Outcome<E, A> = flatMap { a ->
  f(a).map { a }
}

inline fun <A, B, E> Outcome<E, A>.tapFailure(f: (E) -> B): Outcome<E, A> = mapFailure { f(it); it }
inline fun <A, B, E> Outcome<E, A>.tapAbsent(f: () -> B): Outcome<E, A> = onAbsentHandle { f(); Absent }

fun <A, E> Outcome<E, Outcome<E, A>>.flatten() = this.flatMap(::identity)

/**
 * Zip allows you to combine two or more `Outcome`s easily with a supplied function.
 *
 * ```kotlin
 * Present(2).zip(Present(3)) { a, b -> a + b }     // Present(5)
 * Present(2).zip(Absent) { a, b -> a + b }         // Absent
 * Present(2).zip(Failure("nup")) { a, b -> a + b } // Failure("nup")
 * ```
 */
inline fun <E, A, B, C> Outcome<E, A>.zip(other: Outcome<E, B>, f: (A, B) -> C): Outcome<E, C> =
  this.flatMap { a -> other.map { b -> f(a, b) } }

inline fun <E, A, B, C, D> Outcome<E, A>.zip(
  o1: Outcome<E, B>,
  o2: Outcome<E, C>,
  crossinline f: (A, B, C) -> D
): Outcome<E, D> =
  this.zip(o1) { a, b ->
    o2.map { c -> f(a, b, c) }
  }.flatten()

inline fun <E, A, B, C, D, EE> Outcome<E, A>.zip(
  o1: Outcome<E, B>,
  o2: Outcome<E, C>,
  o3: Outcome<E, D>,
  crossinline f: (A, B, C, D) -> EE
): Outcome<E, EE> =
  this.zip(o1, o2) { a, b, c ->
    o3.map { d -> f(a, b, c, d) }
  }.flatten()

inline fun <E, A, B, C, D, EE, F> Outcome<E, A>.zip(
  o1: Outcome<E, B>,
  o2: Outcome<E, C>,
  o3: Outcome<E, D>,
  o4: Outcome<E, EE>,
  crossinline f: (A, B, C, D, EE) -> F
): Outcome<E, F> = outcome {
  f(bind(), o1.bind(), o2.bind(), o3.bind(), o4.bind())
}

/**
 * An extension method on Either<E, Option<A>> that converts it to an Outcome<E, A>.
 *
 * ```
 * Left("bad").toOutcome() // Failure("bad")
 * Right(None).toOutcome() // Absent
 * Right(Some("hi")).toOutcome() // Present("hi")
 * ```
 */
fun <E, A> Either<E, Option<A>>.toOutcome(): Outcome<E, A> = when (this) {
  is Left -> Failure(value)
  is Right -> value.map(::Present).getOrElse { Absent }
}

fun <E, A> Either<E, A>.asOutcome(): Outcome<E, A> = this.map(::Some).toOutcome()

fun <A> Option<A>.toOutcome(): Outcome<Nothing, A> = this.right().toOutcome()

inline fun <A> Outcome<Throwable, A>.orThrow(onAbsent: () -> Throwable): A = when (this) {
  Absent -> throw onAbsent()
  is Failure -> throw this.error
  is Present -> this.value
}

fun <A> Outcome<Throwable, A>.optionOrThrow(): Option<A> = this.inner.orThrow()

/**
 * Converts an Outcome to an option treating Failure as Absent
 */
fun <E, A> Outcome<E, A>.asOption(): Option<A> = inner.getOrElse { None }
inline fun <E, A> Outcome<E, A>.asEither(onAbsent: () -> E): Either<E, A> =
  inner.flatMap { it.map(::Right).getOrElse { onAbsent().left() } }

inline fun <E, A, B> Outcome<E, A>.foldOption(onAbsent: () -> B, onPresent: (A) -> B): Either<E, B> =
  inner.map { it.fold(onAbsent, onPresent) }

inline fun <E, A> Outcome<E, A>.getOrElse(onAbsentOrFailure: () -> A): A =
  this.foldOption(onAbsentOrFailure, ::identity).getOrElse { onAbsentOrFailure() }

inline fun <E, A, B> Outcome<E, A>.fold(onFailure: (E) -> B, onAbsent: () -> B, onPresent: (A) -> B): B = when (this) {
  Absent -> onAbsent()
  is Failure -> onFailure(this.error)
  is Present -> onPresent(this.value)
}

inline fun <E, A> Outcome<E, A>.onAbsentHandle(onAbsent: () -> Outcome<E, A>): Outcome<E, A> =
  when (this) {
    Absent -> onAbsent()
    is Failure -> this
    is Present -> this
  }

inline fun <E, A> Outcome<E, A>.onFailureHandle(onFailure: (E) -> Outcome<E, A>): Outcome<E, A> =
  when (this) {
    Absent -> this
    is Failure -> onFailure(this.error)
    is Present -> this
  }

@OptIn(ExperimentalTypeInference::class)
inline fun <E, A, EE> Outcome<E, A>.recover(@BuilderInference block: OutcomeRaise<EE>.(E) -> A): Outcome<EE, A> =
  when(this) {
    Absent -> Absent
    is Failure -> outcome { block(error) }
    is Present -> this
  }

inline fun <E, A, EE> Outcome<E, A>.mapFailure(f: (E) -> EE): Outcome<EE, A> = when (this) {
  Absent -> Absent
  is Failure -> f(this.error).failure()
  is Present -> this
}

fun <E, A> Outcome<E, Iterable<A>>.sequence(): List<Outcome<E, A>> = when (this) {
  Absent -> listOf(Absent)
  is Failure -> listOf(this)
  is Present -> this.value.map(::Present)
}

fun <E, A> Outcome<E, Option<A>>.sequence(): Option<Outcome<E, A>> = when (this) {
  Absent -> Some(Absent)
  is Failure -> Some(this)
  is Present -> this.value.map(::Present)
}

fun <E, EE, A> Outcome<E, Either<EE, A>>.sequence(): Either<EE, Outcome<E, A>> = when (this) {
  Absent -> Absent.right()
  is Failure -> this.right()
  is Present -> this.value.map(::Present)
}

fun <E, A> Iterable<Outcome<E, A>>.sequence(): Outcome<E, List<A>> =
  outcome { map { it.bind() } }

inline fun <E, A, B> Outcome<E, A>.traverse(f: (A) -> List<B>): List<Outcome<E, B>> = this.map(f).sequence()
inline fun <E, A, B> Outcome<E, A>.traverse(f: (A) -> Option<B>): Option<Outcome<E, B>> = this.map(f).sequence()

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <E, EE, A, B> Outcome<E, A>.traverse(f: (A) -> Either<EE, B>): Either<EE, Outcome<E, B>> = this.map(f).sequence()
