package app.cash.quiver.effects

import app.cash.quiver.Absent
import app.cash.quiver.Failure
import app.cash.quiver.Outcome
import app.cash.quiver.Present
import arrow.core.Either
import arrow.core.left
import arrow.core.merge
import arrow.core.raise.Raise
import arrow.core.raise.eagerEffect
import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.core.right

@JvmInline
value class OutcomeEffectScope<E>(private val cont: Raise<Either<Failure<E>, Absent>>) :
  Raise<Either<Failure<E>, Absent>> {

  suspend fun <B> Outcome<E, B>.bind(): B =
    when (this) {
      Absent -> raise(Absent.right())
      is Failure -> raise(this.left())
      is Present -> value
    }

  override fun raise(r: Either<Failure<E>, Absent>): Nothing = cont.raise(r)
}

@JvmInline
value class OutcomeEagerEffectScope<E>(private val cont: Raise<Either<Failure<E>, Absent>>) :
  Raise<Either<Failure<E>, Absent>> {

  fun <B> Outcome<E, B>.bind(): B =
    when (this) {
      Absent -> raise(Absent.right())
      is Failure -> raise(this.left())
      is Present -> value
    }

  override fun raise(r: Either<Failure<E>, Absent>): Nothing = cont.raise(r)
}

@Suppress("ClassName")
object outcome {
  inline fun <E, A> eager(crossinline f: OutcomeEagerEffectScope<E>.() -> A): Outcome<E, A> =
    eagerEffect {
      f.invoke(OutcomeEagerEffectScope<E>(this))
    }.fold({ it.merge() }, ::Present)

  suspend inline operator fun <E, A> invoke(crossinline f: suspend OutcomeEffectScope<E>.() -> A): Outcome<E, A> =
    effect {
      f.invoke(OutcomeEffectScope<E>(this))
    }.fold({ it.merge() }, ::Present)
}
