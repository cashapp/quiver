package app.cash.quiver.extensions

import app.cash.quiver.arb.ior
import arrow.core.Ior
import arrow.core.leftIor
import arrow.core.rightIor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

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
})
