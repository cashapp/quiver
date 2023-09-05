package app.cash.quiver.extensions

import arrow.core.None
import arrow.core.Option
import arrow.core.left
import arrow.core.nel
import arrow.core.nonEmptyListOf
import arrow.core.right
import arrow.core.some
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeNone
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.StringSpec
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NonEmptyListTest : StringSpec({

  "mapNotNone should map across the NonEmptyList" {
    nonEmptyListOf(1, 2, 3).mapNotNone {
      it.some()
    }.shouldBeSome(nonEmptyListOf(1, 2, 3))
  }

  "mapNotNone should allow for suspended functions" {
    suspend fun intToOption(i: Int): Option<Int> = suspendCoroutine { it.resume(i.some()) }
    nonEmptyListOf(1, 2, 3).mapNotNone {
      intToOption(it)
    }.shouldBeSome(nonEmptyListOf(1, 2, 3))
  }

  "mapNotNone should skip None results" {
    nonEmptyListOf(1, 2, 3).mapNotNone {
      if (it % 2 == 0) {
        None
      } else {
        it.some()
      }
    }.shouldBeSome(nonEmptyListOf(1, 3))
  }

  "mapNotNone should return None if all results are None" {
    nonEmptyListOf(1, 2, 3).mapNotNone {
      None
    }.shouldBeNone()
  }

  "traverseEither maps across the NonEmptyList" {
    nonEmptyListOf(1,2,3).traverseEither {
      (it.toString()).right()
    }.shouldBeRight(nonEmptyListOf("1", "2", "3"))
  }

  "traverseEither short-circuits on Left" {
    nonEmptyListOf(1,2,3,4).traverseEither {
      if (it % 2 == 0)
        it.toString().left()
      else it.right()
    }.shouldBeLeft("2")
  }

  "traverseOption maps Option across the NonEmptyList" {
    nonEmptyListOf(1,2,3).traverseOption {
      (it.toString()).some()
    }.shouldBeSome(nonEmptyListOf("1", "2", "3"))
  }

  "traverseOption short-circuits on None" {
    nonEmptyListOf(1,2,3,4).traverseOption {
      if (it % 2 == 0)
        None
      else it.some()
    }.shouldBeNone()
  }
})
