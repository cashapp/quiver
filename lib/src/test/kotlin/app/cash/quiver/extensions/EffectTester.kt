package app.cash.quiver.extensions

import java.util.concurrent.atomic.AtomicInteger

class EffectTester<T> {
  private lateinit var effectResults: List<suspend () -> T>
  private val effectCallCount = AtomicInteger(0)
  private val effectIndex = AtomicInteger(0)

  /**
   * Each time the effect is invoked, it will return the given result.
   */
  constructor(effectResult: T) {
    this.effectResults = listOf { effectResult }
  }

  /**
   * Each time the effect is invoked, the next result in the list will be provided. When the list is exhausted, it will
   * wrap to the beginning again to provide an infinite sequence.
   */
  constructor(effectResults: List<suspend () -> T>) {
    this.effectResults = effectResults
  }

  /**
   * Each time the effect is invoked, it will return the given result.
   */
  val effect: suspend () -> T = {
    effectCallCount.incrementAndGet()
    // Index the next element in the list and increments the pointer. Will wrap to 0 at the end of the list.
    val result = effectResults[effectIndex.getAndUpdate { (it + 1) % effectResults.size }]()
    result
  }

  fun callCount() = effectCallCount.get()
}
