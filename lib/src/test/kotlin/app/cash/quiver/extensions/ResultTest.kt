package app.cash.quiver.extensions

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class ResultTest : StringSpec({

  "Can transform any success kotlin.Result into a success ErrorOr" {
    checkAll(Arb.string()) {
      Result.success(it).toEither() shouldBeRight it
    }
  }

  "Can transform any failure kotlin.Result into a failure ErrorOr" {
    checkAll(Arb.string().map { Throwable(it) }) {
      Result.failure<Throwable>(it).toEither() shouldBeLeft it
    }
  }

})
