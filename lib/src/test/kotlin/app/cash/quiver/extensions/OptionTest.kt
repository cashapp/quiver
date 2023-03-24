package app.cash.quiver.extensions

import arrow.core.None
import arrow.core.left
import arrow.core.right
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OptionTest : StringSpec({

  "can construct a list of only the somes" {
    listOfSome("a".some(), None, null.some()) shouldBe listOf("a", null)
  }

  "unit will map any some to unit" {
    "orange".some().unit() shouldBe Unit.some()
    None.unit() shouldBe None
  }
})
