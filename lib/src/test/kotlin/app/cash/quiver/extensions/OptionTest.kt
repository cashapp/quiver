package app.cash.quiver.extensions

import arrow.core.None
import arrow.core.right
import arrow.core.some
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.option
import io.kotest.property.checkAll

class OptionTest : StringSpec({

  "can construct a list of only the somes" {
    listOfSome("a".some(), None, null.some()) shouldBe listOf("a", null)
  }

  "unit will map any some to unit" {
    "orange".some().unit() shouldBe Unit.some()
    None.unit() shouldBe None
  }

  "or on Some returns the subject" {
    "orange".some().or("apple".some()) shouldBe "orange".some()
  }

  "or on None returns the supplied value" {
    checkAll(Arb.option(Arb.string())) { other ->
      None.or(other) shouldBe other
    }
  }

  "traverse returns an either of option" {
    "apple".some().traverseOp {
      it.length.right()
    } shouldBeRight (5.some())
  }

  "zip returns an optional combination of optional values" {
    5.some().zipOp("apple".some()) { a: Int, b: String ->
      b.length * a
    } shouldBeSome 25
  }

    "orEmpty returns an empty string if used on a None" {
      None.orEmpty { "I am an useless string " } shouldBe ""
    }

    "orEmpty returns the string supplied if value is Some" {
      "apple".some().orEmpty { "I wanna eat $it" } shouldBe "I wanna eat apple"
    }
})
