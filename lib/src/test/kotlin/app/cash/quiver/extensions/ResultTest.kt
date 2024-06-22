package app.cash.quiver.extensions

import arrow.core.raise.result
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.throwables.shouldThrow
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
      it.failure<Int>() shouldBeFailure it
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

  "Result.catch only catches non-fatal exceptions" {
    Result.catch {
      throw RuntimeException("Non-fatal kaboom!")
    }.shouldBeFailure()

    shouldThrow<OutOfMemoryError> {
      Result.catch {
        throw OutOfMemoryError("Fatal kaboom!")
      }
    }
  }

  "Result.orThrow" {
    Result.success("世界").orThrow() shouldBe "世界"
    shouldThrow<RuntimeException> { Result.failure<Unit>(RuntimeException("thrown")).orThrow() }
  }

  "Result<Result<T>>.flatten" {
    val exception = RuntimeException("thrown")
    Result.success(Result.success("hello")).flatten() shouldBeSuccess "hello"
    Result.success(Result.failure<String>(exception)).flatten() shouldBeFailure exception
    Result.failure<Result<String>>(exception).flatten() shouldBeFailure exception
  }

  "unit will map any success to unit" {
    "orange".success().unit() shouldBe Unit.success()
    val e = RuntimeException("orange")
    e.failure<Int>().unit() shouldBe e.failure()
  }

  "tap performs computation but keeps original value" {
    val right: Result<String> = "hello".success()
    var sideEffect: String? = null

    right.tap { a -> sideEffect = "$a world" }.shouldBeSuccess("hello")
    sideEffect shouldBe "hello world"
  }

  "flatTap performs computation but keeps original value" {
    val e = RuntimeException("banana")
    val right: Result<String> = "hello".success()
    var sideEffect: String? = null

    right.flatTap { a -> result { sideEffect = "$a world" } }.shouldBeSuccess("hello")
    sideEffect shouldBe "hello world"

    right.flatTap { Result.failure<Int>(e) }.shouldBeFailure(e)
    e.failure<Int>().flatTap { Result.failure(Exception("broken")) }.shouldBeFailure(e)
  }

  "Result.isFailure{}" {
    Result.failure<String>(Exception("hello")).isFailure { it.message == "hello" } shouldBe true
    Result.failure<String>(Exception("goodbye")).isFailure { it.message == "hello" } shouldBe false
    "hello".success().isFailure { it.message == "hello" } shouldBe false
  }

  "Result.isSuccess{}" {
    "hello".success().isSuccess { it == "hello" } shouldBe true
    "goodbye".success().isSuccess { it == "hellow" } shouldBe false
    Result.failure<String>(Exception("hello")).isSuccess { it == "hello" } shouldBe false
  }

})
