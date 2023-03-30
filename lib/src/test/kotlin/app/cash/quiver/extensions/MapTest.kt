package app.cash.quiver.extensions

import arrow.core.Some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MapTest : StringSpec({
  "Can retrieve null value key" {
    mapOf(
      "one" to 1,
      "null" to null
    ).getOption("null") shouldBe Some(null)
  }
})
