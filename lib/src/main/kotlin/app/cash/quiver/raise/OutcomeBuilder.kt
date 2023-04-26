package app.cash.quiver.raise

import app.cash.quiver.Absent
import app.cash.quiver.Outcome
import app.cash.quiver.Present
import app.cash.quiver.failure
import app.cash.quiver.present
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
