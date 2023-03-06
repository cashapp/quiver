package app.cash.quiver.extensions

import arrow.core.Option
import arrow.core.nonEmptyListOf
import arrow.core.some
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.StringSpec
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NonEmptyListTest : StringSpec({

  "mapNotNone should allow for suspended functions" {
    suspend fun intToOption(i: Int): Option<Int> = suspendCoroutine { it.resume(i.some()) }
    nonEmptyListOf(1, 2, 3).mapNotNone {
      intToOption(it)
    }.shouldBeSome(nonEmptyListOf(1, 2, 3))
  }
})
