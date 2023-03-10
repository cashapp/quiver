package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
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
})

fun testParse(s: String): ErrorOr<Int> =
  if (s.matches(Regex("-?[0-9]+"))) {
    s.toInt().right()
  } else {
    NumberFormatException("$s is not a valid integer.").left()
  }
