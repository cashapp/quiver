package app.cash.quiver.extensions

import arrow.core.identity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SuspendedFunctionKtTest : StringSpec({
  "map over unitary functions" {
    val f: suspend () -> Int = suspend { 1 }
    f.map { it + 1 }.invoke().shouldBe(2)
  }
  "map identity on unitary functions" {
    val f: suspend () -> Int = suspend { 1 }
    f.map(::identity).invoke().shouldBe(1)
  }

  "map composed functions on unitary functions" {
    val f: suspend () -> Int = suspend { 1 }
    val result1 = f.map { it + 1 }.map { it.toString() }.invoke()
    val result2 = f.map { (it + 1).toString() }.invoke()
    result1 shouldBe result2
  }

  "map over arity 1 functions" {
    val f: suspend (Int) -> Int = { a: Int -> a + 1 }
    f.map { it + 100 }.invoke(1) shouldBe 102
  }

  "map identity on arity 1 functions" {
    val f: suspend (Int) -> Int = { a: Int -> a + 1 }
    f.map(::identity).invoke(1).shouldBe(2)
  }

  "map composed functions on arity 1 functions" {
    val f: suspend (Int) -> Int = { a: Int -> a + 1 }
    val result1 = f.map { it + 1 }.map { it.toString() }.invoke(1)
    val result2 = f.map { (it + 1).toString() }.invoke(1)
    result1 shouldBe result2
  }
})
