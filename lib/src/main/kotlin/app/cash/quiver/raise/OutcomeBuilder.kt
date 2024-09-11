package app.cash.quiver.raise

import app.cash.quiver.Absent
import app.cash.quiver.Absent.inner
import app.cash.quiver.Outcome
import app.cash.quiver.Present
import app.cash.quiver.extensions.ErrorOr
import app.cash.quiver.extensions.OutcomeOf
import app.cash.quiver.extensions.toOutcomeOf
import app.cash.quiver.failure
import app.cash.quiver.present
import app.cash.quiver.toOutcome
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.fold
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

/**
 * DSL build on top of Arrow's Raise for [Outcome].
 *
 * Uses `Raise<Any?>` to provide a slightly optimised builder
 * than nesting `either { option { } }` and not-being able to use `@JvmInline value class`.
 *
 * With context receivers this can be eliminated all together,
 * and `context(Raise<None>, Raise<E>)` or `context(Raise<Null>, Raise<E>)` can be used instead.
 */
@OptIn(ExperimentalTypeInference::class)
inline fun <E, A> outcome(@BuilderInference block: OutcomeRaise<E>.() -> A): Outcome<E, A> =
  fold(
    block = { block(OutcomeRaise(this)) },
    recover = { eOrAbsent ->
      @Suppress("UNCHECKED_CAST")
      if (eOrAbsent === Absent) Absent else (eOrAbsent as E).failure()
    },
    transform = { it.present() }
  )

/**
 * Emulation of _context receivers_,
 * when they're released this can be replaced by _context receiver_ based code in Arrow itself.
 *
 * We guarantee that the wrapped `Any?` will only result in `E` or `Absent`.
 * Exposing this as `Raise<E>` gives natural interoperability with `Raise<E>` DSLs (`Either`).
 */
@OptIn(ExperimentalContracts::class)
class OutcomeRaise<E>(private val raise: Raise<Any?>) : Raise<E> {

  @RaiseDSL
  override fun raise(r: E): Nothing = raise.raise(r)

  @RaiseDSL
  fun <A> Option<A>.bind(): A {
    contract { returns() implies (this@bind is Some<A>) }
    return getOrElse { raise.raise(Absent) }
  }

  @RaiseDSL
  fun <A : Any> ensureNotNull(value: A?): A {
    contract { returns() implies (value != null) }
    return raise.ensureNotNull(value) { Absent }
  }

  @RaiseDSL
  fun ensure(condition: Boolean): Unit {
    contract { returns() implies condition }
    return raise.ensure(condition) { Absent }
  }

  @RaiseDSL
  fun <A> Outcome<E, A>.bind(): A {
    contract { returns() implies (this@bind is Present<A>) }
    return inner.bind().bind()
  }
}

/**
 * DSL build on top of Arrow's Raise for [OutcomeOf].
 *
 * Uses `Raise<Any?>` to provide a slightly optimised builder
 * than nesting `either { option { } }` and not-being able to use `@JvmInline value class`.
 *
 * With context receivers this can be eliminated all together,
 * and `context(Raise<None>, Raise<Throwable>)` or `context(Raise<Null>, Raise<Throwable>)` can be used instead.
 *
 * This is a specialised version and allows interoperability with `Result` as the error side is locked down to
 * `Throwable`.
 */
@OptIn(ExperimentalTypeInference::class)
inline fun <A> outcomeOf(@BuilderInference block: OutcomeOfRaise.() -> A): OutcomeOf<A> =
  fold(
    block = { block(OutcomeOfRaise(this)) },
    recover = { eOrAbsent ->
      @Suppress("UNCHECKED_CAST")
      if (eOrAbsent === Absent) Absent else (eOrAbsent as Throwable).failure()
    },
    transform = { it.present() }
  )

/**
 * Emulation of _context receivers_,
 * when they're released this can be replaced by _context receiver_ based code in Arrow itself.
 *
 * We guarantee that the wrapped `Any?` will only result in `Throwable` or `Absent`.
 * Exposing this as `Raise<Throwable>` gives natural interoperability with `Raise<Throwable>` DSLs (`Either`).
 */
@OptIn(ExperimentalContracts::class)
class OutcomeOfRaise(private val raise: Raise<Any?>) : Raise<Throwable> {

  @RaiseDSL
  override fun raise(r: Throwable): Nothing = raise.raise(r)

  @RaiseDSL
  fun <A> Option<A>.bind(): A {
    contract { returns() implies (this@bind is Some<A>) }
    return getOrElse { raise.raise(Absent) }
  }

  /**
   * Ensures a nullable value is not null. Will raise Absent on null.
   */
  @RaiseDSL
  fun <A> A?.bindNull(): A = ensureNotNull(this)

  /**
   * Converts `Result<Option<A>>` to OutcomeOf<A> and binds over the value
   */
  @RaiseDSL
  fun <A> Result<Option<A>>.bind(): A = toOutcomeOf().bind()

  /**
   * Converts `Result<A>` to OutcomeOf<A> and binds over the value
   */
  @RaiseDSL
  fun <A> Result<A>.bindResult(): A = toOutcome().bind()

  @RaiseDSL
  fun <A : Any> ensureNotNull(value: A?): A {
    contract { returns() implies (value != null) }
    return raise.ensureNotNull(value) { Absent }
  }

  /**
   * Ensures the condition is met and raises an Absent otherwise.
   */
  @RaiseDSL
  fun ensure(condition: Boolean): Unit {
    contract { returns() implies condition }
    return raise.ensure(condition) { Absent }
  }

  @RaiseDSL
  fun <A> OutcomeOf<A>.bind(): A {
    contract { returns() implies (this@bind is Present<A>) }
    return inner.bind().bind()
  }

  /**
   * Converts an ErrorOr<Option<A>> to an OutcomeOf<A> and binds over the value
   */
  @RaiseDSL
  fun <A> ErrorOr<Option<A>>.bindOption(): A {
    return toOutcome().bind()
  }
}
