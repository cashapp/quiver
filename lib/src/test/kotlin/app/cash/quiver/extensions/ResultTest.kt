package app.cash.quiver.extensions

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
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

  "Can transform anything into a success Result" {
    checkAll(Arb.string()) {
      it.success() shouldBeSuccess it
    }
  }

  "Can transform any throwable into a failure Result" {
    checkAll(Arb.string().map { Throwable(it) }) {
      it.failure() shouldBeFailure it
    }
  }

  "toResult converts nullable values into Result" {
    null.toResult { RuntimeException("boo!") }.shouldBeFailure()
    0.toResult { RuntimeException("boo!") } shouldBeSuccess 0
  }

  "mapLeft maps the failure of a Result" {
    val finalException = RuntimeException("Unable to map invalid integer")
    Result.failure<Int>(NumberFormatException("Invalid integer")).mapFailure { finalException } shouldBeFailure
      finalException
  }
})
