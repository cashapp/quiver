package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.core.some
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arrow.core.either
import io.kotest.property.checkAll
import org.junit.jupiter.api.assertThrows

class EitherTest : StringSpec({
  "orThrow returns expected result" {
    assertThrows<NumberFormatException> {
      testParse("hello").orThrow()
    }
  }

  "flatTap performs computation but keeps original value" {
    val right: Either<String, String> = "hello".right()
    val left: Either<String, String> = "goodbye".left()

    right.flatTap { a -> Either.Right("$a world") }.shouldBeRight("hello")
    right.flatTap { a -> Either.Left("$a world") }.shouldBeLeft("hello world")
    left.flatTap { a -> Either.Left("$a world") }.shouldBeLeft("goodbye")
  }

  "toEither will lift a nullable value" {
    val value: String? = null
    value.toEither { "this was null" } shouldBeLeft "this was null"
    val value2: Int? = 123
    value2.toEither { "this is not null" } shouldBeRight 123
  }

  "or will return the first success" {
    Either.Right(1).or { Either.Right(2) } shouldBeRight 1
    Either.Right(1).or { Either.Left("no") } shouldBeRight 1
    Either.Left("no").or { Either.Right(2) } shouldBeRight 2
  }

  "or will return the second failure" {
    Either.Left("no 1").or { Either.Left("no 2") } shouldBeLeft "no 2"
  }

  "mapOption will map the value if Right(Some(t)), otherwise returns the input" {
    Either.Left("error").mapOption<String, Int, Int> { it + 2 } shouldBe Either.Left("error")
    Either.Right<Option<Int>>(None).mapOption { it + 2 } shouldBe Either.Right(None)
    Either.Right(Some(1)).mapOption<String, Int, Int> { it + 2 } shouldBe Either.Right(Some(3))
  }

  "asOption will return an option of the right" {
    Either.Right("zero").asOption() shouldBeSome "zero"
    Either.Left("one").asOption() shouldBe None
  }

  "leftAsOption will return an option of the left" {
    Either.Right("zero").leftAsOption() shouldBe None
    Either.Left("one").leftAsOption() shouldBeSome "one"
  }

  "unit will map any right to unit" {
    "orange".right().unit() shouldBe Unit.right()
    "orange".left().unit() shouldBe "orange".left()
  }

  "validateNotNull is right if value is not null" {
    val value = "test"
    value.validateNotNull().shouldBeRight()
  }

  "validateNotNull is left if value is null" {
    val value: String? = null
    value.validateNotNull().shouldBeLeft(IllegalArgumentException("Value should not be null"))
  }

  "validateNotNull is left if value is null - with label" {
    val value: String? = null
    value.validateNotNull("label".some()).shouldBeLeft(IllegalArgumentException("Value (`label`) should not be null"))
  }
  "zip2" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      a.right().zip(b.right()) { aa, bb -> aa + bb }.shouldBeRight(a + b)
      a.right().zip(b.left()) { aa, bb: Int -> aa + bb }.shouldBeLeft(b)
      a.left().zip(b.right()) { aa: Int, bb -> aa + bb }.shouldBeLeft(a)
      a.left().zip(b.left()) { aa: Int, bb: Int -> aa + bb }.shouldBeLeft(a)
    }
  }
  "zip10" {
    checkAll(
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int()),
      Arb.either(Arb.int(), Arb.int())
    ) { a, b, c, d, e, f, g, h, i, j ->
      val res = a.zip(b, c, d, e, f, g, h, i, j) { aa, bb, cc, dd, ee, ff, gg, hh, ii, jj ->
        aa + bb + cc + dd + ee + ff + gg + hh + ii + jj
      }
      val expected = listOf(a, b, c, d, e, f, g, h, i, j).firstOrNull { it.isLeft() }?.left()
        ?: listOf(a, b, c, d, e, f, g, h, i, j).mapNotNull { it.getOrNull() }.sum().right()

      res shouldBe expected
    }
  }
})

fun testParse(s: String): ErrorOr<Int> =
  if (s.matches(Regex("-?[0-9]+"))) {
    s.toInt().right()
  } else {
    NumberFormatException("$s is not a valid integer.").left()
  }
