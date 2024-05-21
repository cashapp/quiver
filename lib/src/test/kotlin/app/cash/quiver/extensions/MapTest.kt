package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.left
import arrow.core.maybe
import arrow.core.right
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arrow.core.nonEmptyList
import io.kotest.property.checkAll
import app.cash.quiver.extensions.traverse as quiverTraverse

class MapTest : StringSpec({
  "Can retrieve null value key" {
    mapOf(
      "one" to 1,
      "null" to null
    ).getOption("null") shouldBe Some(null)
  }

  "traverse Either" {
    mapOf(
      "1" to 1,
      "2" to 2
    ).quiverTraverse { "$it".right() } shouldBe mapOf(
      "1" to "1",
      "2" to "2"
    ).right()
  }

  "traverseEither" {
    mapOf(
      "1" to 1,
      "2" to 2
    ).traverseEither { "$it".right() } shouldBe mapOf(
      "1" to "1",
      "2" to "2"
    ).right()
  }

  "traverse Either on empty map" {
    emptyMap<String, Int>().quiverTraverse { "$it".right() } shouldBe emptyMap<String, String>().right()
  }

  "traverse Either returns a Left if the function returns a Left" {
    mapOf(
      "1" to 1,
      "2" to 2
    ).quiverTraverse {
      if (it == 2) "error".left() else "$it".right()
    } shouldBe "error".left()
  }

  "traverse Either is stack-safe" {
    val acc = mutableListOf<Int>()
    val res = (0..20_000).associateWith { it }.quiverTraverse { v ->
      acc.add(v)
      Either.Right(v)
    }
    res shouldBe acc.associateWith { it }.right()
    res shouldBe (0..20_000).associateWith { it }.right()
  }

  "traverse Either short-circuit" {
    checkAll(Arb.map(Arb.int(), Arb.int())) { ints ->
      val acc = mutableListOf<Int>()
      val evens = ints.quiverTraverse {
        if (it % 2 == 0) {
          acc.add(it)
          Either.Right(it)
        } else Either.Left(it)
      }
      acc shouldBe ints.values.takeWhile { it % 2 == 0 }
      when (evens) {
        is Either.Right -> evens.value shouldBe ints
        is Either.Left -> evens.value shouldBe ints.values.first { it % 2 != 0 }
      }
    }
  }

  "traverse Option" {
    mapOf(
      "1" to 1,
      "2" to 2
    ).quiverTraverse { "$it".some() } shouldBe mapOf(
      "1" to "1",
      "2" to "2"
    ).some()
  }

  "traverseOption" {
    mapOf(
      "1" to 1,
      "2" to 2
    ).traverseOption { "$it".some() } shouldBe mapOf(
      "1" to "1",
      "2" to "2"
    ).some()
  }

  "traverse Option on empty map" {
    emptyMap<String, Int>().quiverTraverse { "$it".some() } shouldBe emptyMap<String, String>().some()
  }

  "traverse Option returns a None if the function returns a None" {
    mapOf(
      "1" to 1,
      "2" to 2
    ).quiverTraverse {
      if (it == 2) None else "$it".some()
    } shouldBe None
  }

  "traverse Option is stack-safe" {
    // also verifies result order and execution order (l to r)
    val acc = mutableListOf<Int>()
    val res = (0..20_000).associateWith { it }.quiverTraverse { a ->
      acc.add(a)
      Some(a)
    }
    res shouldBe Some(acc.associateWith { it })
    res shouldBe Some((0..20_000).associateWith { it })
  }

  "traverse Option short-circuits" {
    checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
      val acc = mutableListOf<Int>()
      val evens = ints.quiverTraverse {
        if (it % 2 == 0) {
          acc.add(it)
          it.some()
        } else {
          None
        }
      }
      acc shouldBe ints.takeWhile { it % 2 == 0 }
      evens.fold({ Unit }) { it shouldBe ints }
    }
  }
})
