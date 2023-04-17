package app.cash.quiver.extensions

import app.cash.quiver.extensions.Nullable
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class NullableTest : StringSpec({
  "map2 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull()
    ) { a: String?, b: String? ->
      if (a == null || b == null) Nullable.zip(a, b) { _, _ -> Unit } shouldBe null
      else Nullable.zip(a, b) { aa, bb -> aa + bb } shouldBe a + b
    }
  }

  "map3 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull()
    ) { a: String?, b: String?, c: String? ->
      if (a == null || b == null || c == null) Nullable.zip(a, b, c) { aa, bb, cc -> aa + bb + cc } shouldBe null
      else Nullable.zip(a, b, c) { aa, bb, cc -> aa + bb + cc } shouldBe a + b + c
    }
  }

  "map4 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
    ) { a: String?, b: String?, c: String?, d: String? ->
      if (a == null || b == null || c == null || d == null) Nullable.zip(
        a,
        b,
        c,
        d
      ) { aa, bb, cc, dd -> aa + bb + cc + dd } shouldBe null
      else Nullable.zip(a, b, c, d) { aa, bb, cc, dd -> aa + bb + cc + dd } shouldBe a + b + c + d
    }
  }

  "map5 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
    ) { a: String?, b: String?, c: String?, d: String?, e: String? ->
      if (a == null || b == null || c == null || d == null || e == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e
      ) { _, _, _, _, _ -> Unit } shouldBe null
      else Nullable.zip(a, b, c, d, e) { aa, bb, cc, dd, ee -> aa + bb + cc + dd + ee } shouldBe a + b + c + d + e
    }
  }

  "map6 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
    ) { a: String?, b: String?, c: String?, d: String?, e: String?, f: String? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f
      ) { _, _, _, _, _, _ -> Unit } shouldBe null
      else Nullable.zip(a, b, c, d, e, f) { aa, bb, cc, dd, ee, ff -> aa + bb + cc + dd + ee + ff } shouldBe a + b + c + d + e + f
    }
  }

  "map7 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
    ) { a: String?, b: String?, c: String?, d: String?, e: String?, f: String?, g: String? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null || g == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g
      ) { _, _, _, _, _, _, _ -> Unit } shouldBe null
      else Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g
      ) { aa, bb, cc, dd, ee, ff, gg -> aa + bb + cc + dd + ee + ff + gg } shouldBe a + b + c + d + e + f + g
    }
  }

  "map8 only performs action when all arguments are not null" {
    checkAll(
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
      Arb.string().orNull(),
    ) { a: String?, b: String?, c: String?, d: String?, e: String?, f: String?, g: String?, h: String? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null || g == null || h == null) Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g,
        h
      ) { _, _, _, _, _, _, _, _ -> Unit } shouldBe null
      else Nullable.zip(
        a,
        b,
        c,
        d,
        e,
        f,
        g,
        h
      ) { aa, bb, cc, dd, ee, ff, gg, hh -> aa + bb + cc + dd + ee + ff + gg + hh } shouldBe a + b + c + d + e + f + g + h
    }
  }

  "map9 only performs action when all arguments are not null" {
    checkAll(
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull(),
      Arb.int().orNull()
    ) { a: Int?, b: Int?, c: Int?, d: Int?, e: Int?, f: Int?, g: Int?, h: Int?, i: Int? ->
      if (a == null || b == null || c == null || d == null || e == null || f == null || g == null || h == null || i == null) {
        Nullable.zip(a, b, c, d, e, f, g, h, i) { _, _, _, _, _, _, _, _, _ -> Unit } shouldBe null
      } else {
        Nullable.zip(
          a,
          b,
          c,
          d,
          e,
          f,
          g,
          h,
          i
        ) { aa, bb, cc, dd, ee, ff, gg, hh, ii -> aa + bb + cc + dd + ee + ff + gg + hh + ii } shouldBe a + b + c + d + e + f + g + h + i
      }
    }
  }
})
