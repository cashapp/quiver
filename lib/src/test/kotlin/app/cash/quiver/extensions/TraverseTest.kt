package app.cash.quiver.extensions

import arrow.core.Either
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class TraverseTest : StringSpec({
  "traverse a list of integers returns a Right of the list of mapped strings" {
    val result = listOf(1, 2, 3).traverse { Either.Right(it.toString()) }
    result shouldBeRight listOf("1", "2", "3")
  }

  "traverse a set of integers returns a Right of the list of mapped strings" {
    val result = setOf(1, 2, 3).traverse { Either.Right(it.toString()) }
    result shouldBeRight listOf("1", "2", "3")
  }

  "traverse an empty list returns a Right of an empty list" {
    val result = emptyList<Int>().traverse { Either.Right(it) }
    result shouldBeRight emptyList()
  }

  "traverse a list of integers returns a left of an error" {
    val result = listOf(1, 2, 3).traverse { Either.Left("error") }
    result shouldBeLeft "error"
  }

  "the left returned is the first left returned by the function as it maps over the iterable" {
    val result = listOf(1, 2, 4, 6, 7).traverse {
      if (it < 3) Either.Right(it) else Either.Left(it)
    }
    result shouldBeLeft 4
  }
})
