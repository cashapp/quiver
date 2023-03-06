package app.cash.quiver.arb

import app.cash.quiver.Outcome
import app.cash.quiver.toOutcome
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arrow.core.either
import io.kotest.property.arrow.core.option

fun <E, A> Arb.Companion.outcome(error: Arb<E>, value: Arb<A>): Arb<Outcome<E, A>> =
  Arb.either(error, Arb.option(value)).map { it.toOutcome() }
