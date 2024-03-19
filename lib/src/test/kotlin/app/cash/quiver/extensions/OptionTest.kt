package app.cash.quiver.extensions

import app.cash.quiver.extensions.traverse
import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.right
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.option
import io.kotest.property.checkAll
import app.cash.quiver.extensions.traverse as quiverTraverse
import app.cash.quiver.extensions.traverseEither as quiverTraverseEither
import app.cash.quiver.extensions.ifPresent

class OptionTest : StringSpec({

  "can construct a list of only the somes" {
    listOfSome("a".some(), None, null.some()) shouldBe listOf("a", null)
  }

  "unit will map any some to unit" {
    "orange".some().unit() shouldBe Unit.some()
    None.unit() shouldBe None
  }

  "or on Some returns the subject" {
    "orange".some().or { "apple".some() } shouldBe "orange".some()
  }

  "or on None returns the supplied value" {
    checkAll(Arb.option(Arb.string())) { other ->
      None.or { other } shouldBe other
    }
  }

  "orEmpty returns an empty string if used on a None" {
    None.orEmpty { "I am an useless string " } shouldBe ""
  }

  "orEmpty returns the string supplied if value is Some" {
    "apples".some().orEmpty { "I wanna eat $it" } shouldBe "I wanna eat apples"
  }

  "traverse on a Some returns a Right of a Some of the result of the function" {
    Some(42).quiverTraverse { Either.Right("$it") } shouldBe Either.Right(Some("42"))
  }

  "traverse on a Some returns a Left of the result of the function" {
    Some(42).quiverTraverse { Either.Left("$it") } shouldBe Either.Left("42")
  }

  "traverse on None returns a Right of None" {
    None.quiverTraverse { "something".right() } shouldBe Either.Right(None)
  }

  "traverseEither on a Some returns a Right of a Some of the result of the function" {
    Some(42).quiverTraverseEither { Either.Right("$it") } shouldBe Either.Right(Some("42"))
  }

  "traverse to iterable of Some returns a list of the mapped value" {
    Some(42).quiverTraverse { listOf("$it") } shouldBe listOf(Some("42"))
  }

  "ifPresent runs the given side effect and returns a Unit for Some" {
    var sideEffectRun = false
    
    Some(42).ifPresent { sideEffectRun = true } shouldBe Unit
    sideEffectRun shouldBe true
  }

  "ifPresent does not run the given side effect and returns a Unit for None" {
    var sideEffectRun = false

    None.ifPresent { sideEffectRun = true } shouldBe Unit
    sideEffectRun shouldBe false
  }

  @Suppress("UNREACHABLE_CODE")
  "traverse to iterable of None returns an empty list" {
    None.quiverTraverse { listOf(it) } shouldBe emptyList()
  }
})
