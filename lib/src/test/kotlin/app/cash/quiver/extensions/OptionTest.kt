package app.cash.quiver.extensions

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
})
