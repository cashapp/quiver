package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

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

  "traverse an empty list returns a Success of an empty list" {
    val result = emptyList<Int>().traverse { Result.success(it) }
    result shouldBeSuccess emptyList()
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

  "the failure returned is the first failure returned by the function as it maps over the iterable" {
    val result = listOf(1, 2, 4, 6, 7).traverse {
      if (it < 3) Result.success(it) else Result.failure(Throwable(message = it.toString()))
    }
    result.shouldBeFailure().message shouldBe "4"
  }

  "traverseEither a list of integers returns a Right of the list of mapped strings" {
    val result = listOf(1, 2, 3).traverseEither { Either.Right(it.toString()) }
    result shouldBeRight listOf("1", "2", "3")
  }

  "traverseResult a list of integers returns a Success of the list of mapped strings" {
    val result = listOf(1, 2, 3).traverseResult { Result.success(it.toString()) }
    result shouldBeSuccess  listOf("1", "2", "3")
  }

  "traverse a list of integers returns a Some of the list of mapped strings" {
    val result = listOf(1, 2, 3).traverse { Some(it.toString()) }
    result shouldBeSome listOf("1", "2", "3")
  }

  "traverse a set of integers returns a Some of the list of mapped strings" {
    val result = setOf(1, 2, 3).traverse { Some(it.toString()) }
    result shouldBeSome listOf("1", "2", "3")
  }

  "traverse an empty list returns a Some of an empty list" {
    val result = emptyList<Int>().traverse { Some(it.toString()) }
    result shouldBeSome emptyList()
  }

  "traverse a list of integers returns a None if one of the mapped values is None" {
    val result = listOf(1, 2, 3).traverse {
      if (it % 2 == 0) None
      else Some(it.toString())
    }
    result shouldBe None
  }

  "traverseOption a list of integers returns a Some of the list of mapped strings" {
    val result = listOf(1, 2, 3).traverseOption { Some(it.toString()) }
    result shouldBeSome listOf("1", "2", "3")
  }
})
