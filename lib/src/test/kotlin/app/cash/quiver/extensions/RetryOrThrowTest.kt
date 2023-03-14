package app.cash.quiver.extensions

import arrow.core.getOrElse
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class RetryOrThrowTest : StringSpec({

  val runtimeException = RuntimeException("bad")

  "no retries when the effect is successful" {
    val effectTester = EffectTester(true)
    val result = effectTester.effect.withRetriesOrThrow(until = { it.isRight() })
    result shouldBeRight true
    effectTester.callCount() shouldBe 1
  }

  "default until function should check the right side of the either when retrying" {
    val effectTester = EffectTester(true)
    val result = effectTester.effect.withRetriesOrThrow()
    result shouldBeRight true
    effectTester.callCount() shouldBe 1
  }

  "one retry when the first call is unsuccessful" {
    val effectTester = EffectTester(listOf(false, true, true).map { { it } })
    val result = effectTester.effect.withRetriesOrThrow(
      until = { it.getOrElse { false } },
      delay = Duration.ofMillis(1L)
    )
    result shouldBeRight true
    effectTester.callCount() shouldBe 2
  }

  "exhaust retrying when no call is successful" {
    val effectTester = EffectTester(false)
    val result = effectTester.effect.withRetriesOrThrow(
      until = { it.getOrElse { false } },
      additionalTimes = 7,
      delay = Duration.ofMillis(1L)
    )
    result shouldBeRight false
    effectTester.callCount() shouldBe 8
  }

  "retry on error until success" {
    val effectTester = EffectTester(listOf({ throw runtimeException }, { true }))
    val result = effectTester.effect.withRetriesOrThrow(
      until = { it.isRight() },
      delay = Duration.ofMillis(1)
    ).orThrow()
    result shouldBe true
    effectTester.callCount() shouldBe 2
  }

  "retry on error until exhausted" {
    val effectTester = EffectTester<Boolean>(listOf { throw runtimeException })
    shouldThrow<RuntimeException> {
      effectTester.effect.withRetriesOrThrow(
        until = { it.isRight() },
        additionalTimes = 3,
        delay = Duration.ofMillis(1)
      ).orThrow()
    }
    effectTester.callCount() shouldBe 4
  }

  "be able to ignore expected errors when retrying" {
    val effectTester = EffectTester<Boolean>(listOf { throw runtimeException })
    shouldThrow<RuntimeException> {
      effectTester.effect.withRetriesOrThrow(
        until = { it.isLeft() },
        delay = Duration.ofMillis(1)
      ).orThrow()
    }
    effectTester.callCount() shouldBe 1
  }

})
