package app.cash.quiver

import app.cash.quiver.arb.outcome
import app.cash.quiver.continuations.outcome
import app.cash.quiver.matchers.shouldBeAbsent
import app.cash.quiver.matchers.shouldBeFailure
import app.cash.quiver.matchers.shouldBePresent
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.core.some
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeNone
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.either
import io.kotest.property.arrow.core.option
import io.kotest.property.checkAll
import kotlinx.coroutines.coroutineScope

class OutcomeTest : StringSpec({
  "Present flatMap" {
    Present("hi").flatMap { Present("$it world") }.shouldBePresent().shouldBe("hi world")
  }
  "Present flatMap absent" {
    Present("hi").flatMap { Absent }.shouldBeAbsent()
  }
  "Present flatMap error" {
    Present("hi").flatMap { Failure("boo") }.shouldBeFailure()
  }

  "Failure flatMap absent" {
    Failure("bad").flatMap { Absent }.shouldBeFailure()
  }

  "Present flatMap with suspended function" {
    Present("hi").flatMap { coroutineScope { Present("$it world") } }
      .shouldBePresent().shouldBe("hi world")
  }

  "Absent flatMap error" {
    Absent.flatMap { Failure("bad") }.shouldBeAbsent()
  }

  "Absent shouldBePresent throws" {
    shouldThrow<AssertionError> {
      Absent.map { "nothing" }.shouldBePresent()
    }.message shouldBe "Expecting Present, got Absent"
  }

  "Present map" {
    "hi".present().map { "$it world" }.shouldBePresent().shouldBe("hi world")
  }
  "Absent map" {
    Absent.map { "what?" }.shouldBeAbsent()
  }
  "Failure map" {
    Failure("bad").map { "don't even" }.shouldBeFailure()
  }

  "bind over present" {
    outcome.eager {
      val a = Present("1").bind()
      a
    }.shouldBePresent().shouldBe("1")
  }

  "bind over suspended function" {
    runBlocking {
      val outcome1: Outcome<String, Int> = Present(1)
      outcome {
        val a = outcome1.bind()
        val b = Present(2).bind()
        a + b
      }.shouldBePresent().shouldBe(3)
    }
  }

  "Monad laws: right identity" {
    checkAll(Arb.outcome(Arb.string(), Arb.int())) { outcome ->
      outcome.flatMap { a -> Present(a) }.shouldBe(outcome)
    }
  }
  "Monad laws: right identity (bind)" {
    checkAll(Arb.outcome(Arb.string(), Arb.int())) { outcome ->
      outcome {
        val a = outcome.bind()
        Present(a).bind()
      }.shouldBe(outcome)
    }
  }
  "Monad laws: left identity" {
    checkAll(Arb.int()) { value ->
      val f = { a: Int ->
        when (a % 3) {
          0 -> Failure("doh")
          1 -> Absent
          2 -> Present(a)
          else -> Absent
        }
      }
      Present(value).flatMap(f).shouldBe(f(value))
    }
  }

  "Monad laws: left identity (bind)" {
    checkAll(Arb.int()) { value ->
      val f = { a: Int ->
        when (a % 3) {
          0 -> Failure("doh")
          1 -> Absent
          2 -> Present(a)
          else -> Absent
        }
      }
      outcome {
        val a = Present(value).bind()
        f(a).bind()
      }.shouldBe(f(value))
    }
  }

  "Monad laws: associativity" {
    checkAll(Arb.outcome(Arb.string(), Arb.int())) { outcome ->
      val f = { a: Int ->
        when (a % 3) {
          0 -> Failure("doh")
          1 -> Absent
          2 -> Present(a)
          else -> Absent
        }
      }
      val g = { a: Int ->
        when ((a + 1) % 3) {
          0 -> Failure("doh")
          1 -> Absent
          2 -> Present("$a")
          else -> Absent
        }
      }

      outcome.flatMap(f).flatMap(g).shouldBe(outcome.flatMap { f(it).flatMap(g) })
    }
  }

  "catch throwable" {
    Outcome.catch {
      throw RuntimeException("yikes")
    }.shouldBeFailure().let { it.message.shouldBe("yikes") }
  }

  "catch value" {
    Outcome.catch { 1 }.shouldBePresent().shouldBe(1)
  }

  "catchOption throwable" {
    Outcome.catchOption<Nothing> {
      throw RuntimeException("doh!")
    }.shouldBeFailure().let { it.message.shouldBe("doh!") }
  }

  "catchOption Some" {
    Outcome.catchOption {
      1.some()
    }.shouldBePresent().shouldBe(1)
  }

  "catchOption None" {
    Outcome.catchOption {
      None
    }.shouldBeAbsent()
  }

  "Lift Either<E,A> into Outcome" {
    1.right().asOutcome().shouldBePresent().shouldBe(1)
    "bad".left().asOutcome().shouldBeFailure().shouldBe("bad")
  }

  "Lift Either<E,Option<A>> into Outcome" {
    1.some().right().toOutcome().shouldBePresent().shouldBe(1)
    None.right().toOutcome().shouldBeAbsent()
    "bad".left().toOutcome<String, Option<Int>>().shouldBeFailure()
  }

  "knock stuff into shape" {
    val maybeResult: Outcome<String, Int> = 1.present()
    val definiteResult: Either<String, Int> = 1.right()
    val optionalResult: Either<String, Option<Int>> = 1.some().right()

    outcome {
      val a = maybeResult.bind()
      val b = definiteResult.asOutcome().bind()
      val c = optionalResult.toOutcome().bind()
      a + b + c
    }.shouldBePresent().shouldBe(3)
  }

  "zip should work like bind" {
    val outcomeGen = Arb.outcome(Arb.string(), Arb.int())
    val f = { a: Int, b: Int -> a + b }
    checkAll(outcomeGen, outcomeGen) { o1, o2 ->
      val result1 = o1.zip(o2, f)
      val result2 = outcome {
        f(o1.bind(), o2.bind())
      }
      result1.shouldBe(result2)
    }
  }
  "zip2 should work like bind" {
    val outcomeGen = Arb.outcome(Arb.string(), Arb.int())
    val f = { a: Int, b: Int, c: Int -> a + b + c }
    checkAll(outcomeGen, outcomeGen, outcomeGen) { o1, o2, o3 ->
      val result1 = o1.zip(o2, o3, f)
      val result2 = outcome {
        f(o1.bind(), o2.bind(), o3.bind())
      }
      result1.shouldBe(result2)
    }
  }
  "zip3 should work like bind" {
    val outcomeGen = Arb.outcome(Arb.string(), Arb.int())
    val f = { a: Int, b: Int, c: Int, d: Int -> a + b + c + d }
    checkAll(outcomeGen, outcomeGen, outcomeGen, outcomeGen) { o1, o2, o3, o4 ->
      val result1 = o1.zip(o2, o3, o4, f)
      val result2 = outcome {
        f(o1.bind(), o2.bind(), o3.bind(), o4.bind())
      }
      result1.shouldBe(result2)
    }
  }
  "zip4 should work like bind" {
    val outcomeGen = Arb.outcome(Arb.string(), Arb.int())
    val f = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
    checkAll(outcomeGen, outcomeGen, outcomeGen, outcomeGen, outcomeGen) { o1, o2, o3, o4, o5 ->
      val result1 = o1.zip(o2, o3, o4, o5, f)
      val result2 = outcome {
        f(o1.bind(), o2.bind(), o3.bind(), o4.bind(), o5.bind())
      }
      result1.shouldBe(result2)
    }
  }

  "asOption throws away errors" {
    "bad".failure().asOption() shouldBe None
    1.present().asOption() shouldBeSome 1
    Absent.asOption() shouldBe None
  }

  "lifts option to outcome" {
    Some(1).toOutcome().shouldBePresent().shouldBe(1)
    None.toOutcome().shouldBeAbsent()
  }

  "orThrow retrieves value" {
    1.present().orThrow { RuntimeException("yikes") }.shouldBe(1)
  }
  "orThrow throws exception on absent" {
    shouldThrow<Throwable> { Absent.orThrow<Int> { RuntimeException("yikes") } }
  }

  "orThrow throws exception on failure" {
    shouldThrow<Throwable> {
      RuntimeException("serious yikes").failure().orThrow<Int> { RuntimeException("yikes") }
    }.message shouldBe "serious yikes"
  }

  "optionOrThrow return option or throws any failure exceptions" {
    Absent.optionOrThrow().shouldBeNone()
    Present(1).optionOrThrow().shouldBeSome(1)
    shouldThrow<Throwable> { Failure(RuntimeException("yikes")).optionOrThrow() }
  }

  "asEither converts to Either converting Absent to an error" {
    1.present().asEither { "nup" }.shouldBeRight(1)
    Absent.asEither { "nup" }.shouldBeLeft("nup")
    "bad".failure().asEither { "nup" }.shouldBeLeft("bad")
  }

  "getOrElse throws away the absence and failures and uses default" {
    1.present().getOrElse { 2 }.shouldBe(1)
    Absent.getOrElse { 2 }.shouldBe(2)
    Failure("bad").getOrElse { 2 }.shouldBe(2)
  }

  "foldOption performs a fold over the optional side (Absent or Present)" {
    1.present().foldOption({ 2 }, { 3 }).shouldBeRight(3)
    Absent.foldOption({ 2 }, { 3 }).shouldBeRight(2)
    Failure("bad").foldOption({ 2 }, { 3 }).shouldBeLeft("bad")
  }

  "fold collapses the outcome" {
    1.present().fold({ 1 }, { 2 }, { 3 }).shouldBe(3)
    Absent.fold({ 1 }, { 2 }, { 3 }).shouldBe(2)
    Failure("bad").fold({ 1 }, { 2 }, { 3 }).shouldBe(1)
  }

  "recover from failure with onFailureHandle" {
    "bad".failure().onFailureHandle { "not so $it".present() }.shouldBePresent().shouldBe("not so bad")
    "ok".present().onFailureHandle { "nope".present() }.shouldBePresent().shouldBe("ok")
    Absent.onFailureHandle { "nope".present() }.shouldBeAbsent()
  }
  "recover from absent with onAbsentHandle" {
    "bad".failure().onAbsentHandle { "not so".present() }.shouldBeFailure().shouldBe("bad")
    "ok".present().onAbsentHandle { "nope".present() }.shouldBePresent().shouldBe("ok")
    Absent.onAbsentHandle { "yes".present() }.shouldBePresent().shouldBe("yes")
  }

  "sequence over a list" {
    listOf(1.present(), 2.present(), 3.present()).sequence().shouldBePresent() shouldBe listOf(1, 2, 3)
    listOf(1.present(), 2.present(), Absent, 3.present(), "bad".failure()).sequence().shouldBeAbsent()
    listOf(1.present(), 2.present(), 3.present(), "bad".failure(), Absent).sequence().shouldBeFailure().shouldBe("bad")
  }

  "Either sequence/traverse" {
    Present(1.right()).sequence().shouldBeRight().shouldBePresent().shouldBe(1)
    Present(1.right()).sequence() shouldBe Present(1).traverse { a: Int -> a.right() }

    val bad: Outcome<String, Either<String, Int>> = "bad".failure()
    bad.sequence().shouldBeRight().shouldBeFailure().shouldBe("bad")

    Present("oops".left()).sequence().shouldBeLeft() shouldBe "oops"

    val absent: Outcome<String, Either<String, Int>> = Absent
    absent.sequence().shouldBeRight().shouldBeAbsent()
  }

  "Option sequence/traverse" {
    Present(1.some()).sequence().shouldBeSome().shouldBePresent().shouldBe(1)
    Present(1.some()).sequence() shouldBe Present(1).traverse { a: Int -> a.some() }

    val bad: Outcome<String, Option<Int>> = None.present()
    bad.sequence().shouldBeNone()

    val absent: Outcome<String, Option<Int>> = Absent
    absent.sequence().shouldBeSome().shouldBeAbsent()
  }

  "List sequence/traverse" {
    Present(listOf(1, 2, 3)).sequence().shouldBe(listOf(1.present(), 2.present(), 3.present()))
    Present(listOf(1, 1)).sequence() shouldBe Present(1).traverse { a: Int -> listOf(a, a) }

    val bad: Outcome<String, List<Int>> = "bad".failure()
    bad.sequence() shouldBe listOf("bad".failure())

    val absent: Outcome<String, List<Int>> = Absent
    absent.sequence() shouldBe listOf(Absent)
  }

  "mapFailure" {
    Present(1).mapFailure { _: String -> 2 }.shouldBePresent().shouldBe(1)
    Absent.mapFailure { "cat" }.shouldBeAbsent()
    "bad".failure().mapFailure { "$it failure" }.shouldBeFailure().shouldBe("bad failure")
  }

  "tap performs effect" {
    var value = 0
    1.present().tap { value = 2 }.shouldBePresent().shouldBe(1)
    value shouldBe 2
    1.failure().tap { value = 3 }.shouldBeFailure().shouldBe(1)
    value shouldBe 2
    Absent.tap { value = 4 }.shouldBeAbsent()
    value shouldBe 2
  }

  "flatTap binds over outcome but leaves value intact" {
    1.present().flatTap { Absent }.shouldBeAbsent()
    1.present().flatTap { 2.failure() }.shouldBeFailure()
    1.present().flatTap { 2.present() }.shouldBePresent().shouldBe(1)
  }

  "tapAbsent performs an effect only on Absents" {
    var value = 0
    Absent.tapAbsent { value = 1 }.shouldBeAbsent()
    value shouldBe 1
  }

  "tapAbsent ignores Present and Failure" {
    var value = 0
    "hi".present().tapAbsent { value = 1 }.shouldBePresent()
    value shouldBe 0
    "bad".failure().tapAbsent { value = 2 }.shouldBeFailure()
    value shouldBe 0
  }

  "tapFailure performs an effect only on Failures" {
    var value = 0
    "bad".failure().tapFailure { value = 1 }.shouldBeFailure()
    value shouldBe 1
  }

  "tapFailure ignores Present and Absent" {
    var value = 0
    "hi".present().tapFailure { value = 1 }.shouldBePresent()
    value shouldBe 0
    Absent.tapFailure { value = 2 }.shouldBeAbsent()
    value shouldBe 0
  }
  "filter converts keeps the value if the predicate is true otherwise returns Absent" {
    fun lessThan5(i: Int) = i < 5
    1.present().filter(::lessThan5).shouldBePresent().shouldBe(1)
    5.present().filter(::lessThan5).shouldBeAbsent()
    Absent.filter(::lessThan5).shouldBeAbsent()
    "bad".failure().filter(::lessThan5).shouldBeFailure().shouldBe("bad")
  }
  "raise - Outcome bind identity" {
    checkAll(Arb.outcome(Arb.string(), Arb.int())) { original ->
      app.cash.quiver.raise.outcome {
        val a = original.bind()
        a
      }.shouldBe(original)
    }
  }
  "raise - Either bind identity" {
    checkAll(Arb.either(Arb.string(), Arb.int())) { original ->
      app.cash.quiver.raise.outcome {
        val a = original.bind()
        a
      }.shouldBe(original.asOutcome())
    }
  }
  "raise - Option bind identity" {
    checkAll(Arb.option(Arb.int())) { original ->
      app.cash.quiver.raise.outcome<String, Int> {
        val a = original.bind()
        a
      }.shouldBe(original.toOutcome())
    }
  }
  "recover ignores Present and Absent" {
    checkAll(Arb.outcome(Arb.string(), Arb.int())) { fallback ->
      1.present().recover { fallback.bind() }.shouldBePresent().shouldBe(1)
      val absent: Outcome<String, Int> = Absent
      absent.recover { fallback.bind() }.shouldBeAbsent()
    }
  }
  "recover can recover from Failure" {
    checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
      val x: Outcome<String, Int> = "failure".failure()
      x.recover { either.bind() }
        .asEither { fail("Cannot be absent") }.shouldBe(either)
    }
  }
})
