package app.cash.quiver.extensions

import arrow.core.None
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OptionTest : StringSpec({

  "can construct a list of only the somes" {
    listOfSome("a".some(), None, "c".some()) shouldBe listOf("a", "c")
  }
})
