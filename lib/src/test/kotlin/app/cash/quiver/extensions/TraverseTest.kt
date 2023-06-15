package app.cash.quiver.extensions

import arrow.core.Either
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class TraverseTest : StringSpec({
  "traverse a list of integer to an either of a list of string" {
    val result = listOf(1, 2, 3).traverse { Either.Right(it.toString()) }
    result shouldBeRight listOf("1", "2", "3")
  }

  "traverse a set of integer to an either of a list of string" {
    val result = setOf(1, 2, 3).traverse { Either.Right(it.toString()) }
    result shouldBeRight listOf("1", "2", "3")
  }

  "traverse an empty list returns an empty list" {
    val result: Either<Nothing, List<Int>> = emptyList<Int>().traverse { Either.Right(it) }
    result shouldBeRight emptyList()
  }

  "fail to traverse a list of integer returns error" {
    val result = listOf(1, 2, 3).traverse { Either.Left("error") }
    result shouldBeLeft "error"
  }

  "traverse return the integer itself when it does not meet the condition ( int < 3  )" {
    val result = listOf(1, 2, 4, 6, 7).traverse {
      if (it < 3) Either.Right(it) else Either.Left(it)
    }
    result shouldBeLeft 4
  }
})
