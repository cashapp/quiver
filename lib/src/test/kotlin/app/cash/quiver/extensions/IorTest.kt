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
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int()),
      Arb.ior(Arb.int(), Arb.int())
    ) { a, b, c, d, e, f, g, h, i, j ->
      val res = a.zip(Int::plus, b, c, d, e, f, g, h, i, j) { aa, bb, cc, dd, ee, ff, gg, hh, ii, jj ->
        aa + bb + cc + dd + ee + ff + gg + hh + ii + jj
      }

      val all = listOf(a, b, c, d, e, f, g, h, i, j)
      val allLeft = all.mapNotNull { it.leftOrNull() }
      val allRight = all.mapNotNull { it.orNull() }

      val expected = when {
        allLeft.isEmpty() -> allRight.sum().rightIor()
        allRight.isEmpty() -> allLeft.sum().leftIor()
        else -> Ior.Both(allLeft.sum(), allRight.sum())
      }

      res shouldBe expected
    }
  }
})
