package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import app.cash.quiver.extensions.traverse as quiverTraverse

class SequenceTest : StringSpec({

  "traverse Either on an empty sequence returns an empty list" {
    emptySequence<Int>().quiverTraverse { Either.Left(RuntimeException()) } shouldBe emptyList<Int>().right()
  }

  "traverse Either on a sequence returns a Right of the mapped list" {
    (0..9).asSequence().quiverTraverse { Either.Right(it) } shouldBe (0..9).toList().right()
  }

  "traverse Either on a sequence returns a Left if the mapped function returns a Left" {
    val error = RuntimeException("boom")
    (0..9).asSequence().quiverTraverse {
      if (it == 5) Either.Left(error) else Either.Right(it)
    } shouldBe error.left()
  }

  "traverseEither is a synonym for traverse Either" {
    emptySequence<Int>().traverseEither { Either.Left(RuntimeException()) } shouldBe emptyList<Int>().right()
  }

  "traverse for Either stack-safe" {
    // also verifies result order and execution order (l to r)
    val acc = mutableListOf<Int>()
    val res = generateSequence(0) { it + 1 }.quiverTraverse { a ->
      if (a > 20_000) {
        Either.Left(Unit)
      } else {
        acc.add(a)
        Either.Right(a)
      }
    }
    acc shouldBe (0..20_000).toList()
    res shouldBe Either.Left(Unit)
  }

  "traverse Option on an empty sequence returns an empty list" {
    emptySequence<Int>().quiverTraverse { None } shouldBe emptyList<Int>().some()
  }

  "traverse Option on a sequence returns a Some of the mapped list" {
    (0..9).asSequence().quiverTraverse { Some(it) } shouldBe (0..9).toList().some()
  }

  "traverse Option on a sequence returns a None if the mapped function returns a None" {
    (0..9).asSequence().quiverTraverse {
      if (it == 5) None else Some(it)
    } shouldBe None
  }

  "traverseOption is a synonym for traverse Option" {
    emptySequence<Int>().traverseOption { Some(it) } shouldBe emptyList<Int>().some()
  }

  "traverse for Option stack-safe" {
    // also verifies result order and execution order (l to r)
    val acc = mutableListOf<Int>()
    val res = generateSequence(0) { it + 1 }.quiverTraverse { a ->
      if (a > 20_000) {
        None
      } else {
        acc.add(a)
        Some(a)
      }
    }
    acc shouldBe (0..20_000).toList()
    res shouldBe None
  }
})
