package app.cash.quiver

import app.cash.quiver.extensions.ErrorOr
import app.cash.quiver.matchers.shouldBeAbsent
import app.cash.quiver.matchers.shouldBeFailure
import app.cash.quiver.matchers.shouldBePresent
import app.cash.quiver.raise.outcomeOf
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OutcomeOfRaiseTest : StringSpec({

  "outcomeOf success path" {
    outcomeOf {
      val a = Present(1).bind()
      val b: Int = Result.success(1.some()).bind()
      val c: Int = Either.Right(2).bind()
      val d: Int = Either.Right(4.some()).bindOption()
      val nullValue: Int? = 1
      val e: Int = ensureNotNull(nullValue)
      val f: Int = nullValue.bindNull()
      val g: Int = Result.success(1).bindResult()
      val result = a + b + c + d + e + f + g
      ensure(result > 9)
      result
    }.shouldBePresent().shouldBe(11)
  }

  "outcomeOf Either failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val b: Int = Either.Left(Throwable("doh")).bind<Int>()
      a + b
    }.shouldBeFailure().message shouldBe "doh"
  }

  "outcomeOf Option failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val b: Int = None.bind<Int>()
      a + b
    }.shouldBeAbsent()
  }
  "outcomeOf Nullable failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val b: Int? = null
      a + (b.bindNull())
    }.shouldBeAbsent()
  }

  "outcomeOf Result<Option<A>> failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val failure:Result<Option<Int>> = Result.failure<Option<Int>>(Throwable("doh"))
      val b: Int = failure.bind()
      a + b
    }.shouldBeFailure().message shouldBe "doh"
  }

  "outcomeOf Result<None> failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val failure:Result<Option<Int>> = Result.success(None)
      val b: Int = failure.bind()
      a + b
    }.shouldBeAbsent()
  }

  "outcomeOf Result<A> failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val failure:Result<Int> = Result.failure(Throwable("doh"))
      val b: Int = failure.bindResult()
      a + b
    }.shouldBeFailure().message shouldBe "doh"
  }

  "outcomeOf ErrorOr<Option<A>> failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val failure: ErrorOr<Option<Int>> = Either.Left(Throwable("doh"))
      val b: Int = failure.bindOption()
      a + b
    }.shouldBeFailure().message shouldBe "doh"
  }

  "outcomeOf ErrorOr<A> failure path" {
    outcomeOf {
      val a = Present(1).bind()
      val failure: ErrorOr<Int> = Either.Left(Throwable("doh"))
      val b: Int = failure.bind()
      a + b
    }.shouldBeFailure().message shouldBe "doh"
  }

  "outcomeOf 'ensure' failure path" {
    outcomeOf {
      val a = Present(3).bind()
      ensure(a > 10)
    }.shouldBeAbsent()
  }

})
