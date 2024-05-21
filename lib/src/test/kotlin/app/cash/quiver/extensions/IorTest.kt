package app.cash.quiver.extensions

import app.cash.quiver.arb.ior
import arrow.core.Ior
import arrow.core.None
import arrow.core.left
import arrow.core.right
import arrow.core.some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import app.cash.quiver.extensions.traverse as quiverTraverse
import app.cash.quiver.extensions.traverseEither as quiverTraverseEither
import app.cash.quiver.extensions.traverseOption as quiverTraverseOption

class IorTest : StringSpec({
  "zip10" {
    checkAll(
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int()),
      Arb.ior(Arb.int(0..100), Arb.int())
    ) { a, b, c, d, e, f, g, h, i, j ->
      val res = a.zip(Int::plus, b, c, d, e, f, g, h, i, j) { aa, bb, cc, dd, ee, ff, gg, hh, ii, jj ->
        aa + bb + cc + dd + ee + ff + gg + hh + ii + jj
      }
      val all = listOf(a, b, c, d, e, f, g, h, i, j)
      val expected = all.fold<Ior<Int, Int>, Ior<Int, Int>>(Ior.Right(0)) { acc, curr ->
        when (acc) {
          is Ior.Left -> acc // stop accumulating if a Left is encountered
          is Ior.Right -> curr.fold(
            { Ior.Left(it) },
            { Ior.Right(acc.value + it) },
            { l, r -> Ior.Both(l, acc.value + r) }
          )

          is Ior.Both -> curr.fold(
            { Ior.Left(acc.leftValue + it) },
            { Ior.Both(acc.leftValue, it + acc.rightValue) },
            { l, r -> Ior.Both(acc.leftValue + l, acc.rightValue + r) }
          )
        }
      }

      res shouldBe expected
    }
  }

  "traverse Either on an Left returns an Either.Right of the Left" {
    Ior.Left(1).quiverTraverse { "A".right() } shouldBe Ior.Left(1).right()
  }

  "traverse Either on a Right returns an Either.Right of the Right" {
    Ior.Right(1).quiverTraverse { "A".right() } shouldBe Ior.Right("A").right()
  }

  "traverse Either on a Both returns an Either.Right of the Both with mapped Right" {
    Ior.Both(1, 2).quiverTraverse { "A".right() } shouldBe Ior.Both(1, "A").right()
  }

  "traverseEither synonym for traverse Either" {
    Ior.Both(1, 2).quiverTraverseEither { "A".right() } shouldBe Ior.Both(1, "A").right()
  }

  "traverse Either on a Right returns an Either.Left if the function returns an Either.Left" {
    Ior.Right(1).quiverTraverse { "A".left() } shouldBe "A".left()
  }

  "traverse Either on a Both returns an Either.Left if the function returns an Either.Left" {
    Ior.Both(1, 2).quiverTraverse { "A".left() } shouldBe "A".left()
  }

  "traverse Option on an Left returns an Some of the Left" {
    Ior.Left(1).quiverTraverse { "A".some() } shouldBe Ior.Left(1).some()
  }

  "traverse Option on a Right returns an Some of the Right" {
    Ior.Right(1).quiverTraverse { "A".some() } shouldBe Ior.Right("A").some()
  }

  "traverse Option on a Both returns an Some of the Both with mapped Right" {
    Ior.Both(1, 2).quiverTraverse { "A".some() } shouldBe Ior.Both(1, "A").some()
  }

  "traverseOption synonym for traverse Option" {
    Ior.Both(1, 2).quiverTraverseOption { "A".some() } shouldBe Ior.Both(1, "A").some()
  }

  "traverse Option on a Right returns a None if the function returns a None" {
    Ior.Right(1).quiverTraverse { None } shouldBe None
  }

  "traverse Option on a Both returns a None if the function returns a None" {
    Ior.Both(1, 2).quiverTraverse { None } shouldBe None
  }


  "traverse Iterable on an Left returns a List of the Left" {
    Ior.Left(1).quiverTraverse { listOf("A", "B") } shouldBe listOf(Ior.Left(1))
  }

  "traverse Iterable on a Right returns a List of the Right" {
    Ior.Right(1).quiverTraverse { listOf("A", "B") } shouldBe listOf(Ior.Right("A"), Ior.Right("B"))
  }

  "traverse Iterable on a Both returns a List of Boths with mapped Rights" {
    Ior.Both(1, 2).quiverTraverse { listOf("A", "B") } shouldBe
      listOf(Ior.Both(1, "A"), Ior.Both(1, "B"))
  }
})
