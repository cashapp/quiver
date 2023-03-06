package app.cash.quiver.continuations

import arrow.core.Either
import arrow.core.continuations.EagerEffectScope
import arrow.core.continuations.EffectScope
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.effect
import arrow.core.left
import arrow.core.merge
import arrow.core.right
import app.cash.quiver.Absent
import app.cash.quiver.Failure
import app.cash.quiver.Outcome
import app.cash.quiver.Present

@JvmInline
value class OutcomeEagerEffectScope<E>(private val cont: EagerEffectScope<Either<Failure<E>, Absent>>) :
  EagerEffectScope<Either<Failure<E>, Absent>> {

  suspend fun <B> Outcome<E, B>.bind(): B =
    when (this) {
      is Absent -> shift(Absent.right())
      is Failure -> shift(this.left())
      is Present -> value
    }

  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: Either<Failure<E>, Absent>): B = cont.shift(r)
}

@JvmInline
public value class OutcomeEffectScope<E>(private val cont: EffectScope<Either<Failure<E>, Absent>>) :
  EffectScope<Either<Failure<E>, Absent>> {

  public suspend fun <B> Outcome<E, B>.bind(): B =
    when (this) {
      Absent -> shift(Absent.right())
      is Failure -> shift(this.left())
      is Present -> value
    }

//  public suspend fun ensure(value: Boolean): Unit =
//    ensure(value) { None }

  override suspend fun <B> shift(r: Either<Failure<E>, Absent>): B =
    cont.shift(r)
}

@Suppress("ClassName")
object outcome {
  inline fun <E, A> eager(crossinline f: suspend OutcomeEagerEffectScope<E>.() -> A): Outcome<E, A> =
    eagerEffect {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(OutcomeEagerEffectScope(this))
    }.fold({ it.merge() }, ::Present)

  suspend inline operator fun <E, A> invoke(crossinline f: suspend OutcomeEffectScope<E>.() -> A): Outcome<E, A> =
    effect { f(OutcomeEffectScope(this)) }.fold({ it.merge() }, ::Present)
}
