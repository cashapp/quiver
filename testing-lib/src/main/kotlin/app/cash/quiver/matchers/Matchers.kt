package app.cash.quiver.matchers

import app.cash.quiver.Absent
import app.cash.quiver.Failure
import app.cash.quiver.Outcome
import app.cash.quiver.Present
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <A, B> Outcome<A, B>.shouldBePresent(failureMessage: (A) -> String = { "Expected Outcome.Present, but found $it" }): B {
  contract {
    returns() implies (this@shouldBePresent is Present<B>)
  }
  return when (this) {
    Absent -> throw AssertionError("Expecting Present, got Absent")
    is Failure -> throw AssertionError(failureMessage(error))
    is Present -> value
  }
}

@OptIn(ExperimentalContracts::class)
fun <A, B> Outcome<A, B>.shouldBeAbsent(failureMessage: (A) -> String = { "Expected Outcome.Absent, but found $it" }) {
  contract {
    returns() implies (this@shouldBeAbsent is Absent)
  }
  return when (this) {
    Absent -> Unit
    is Failure -> throw AssertionError(failureMessage(error))
    is Present -> throw AssertionError("Got present, expected Absent ($this)")
  }
}

@OptIn(ExperimentalContracts::class)
fun <A, B> Outcome<A, B>.shouldBeFailure(): A {
  contract {
    returns() implies (this@shouldBeFailure is Failure<A>)
  }
  return when (this) {
    Absent -> throw AssertionError("Expected Outcome.Failure but got Absent")
    is Failure -> error
    is Present -> throw AssertionError("Expected Outcome.Failure but got Present($value)")
  }
}
