package app.cash.quiver.extensions

import arrow.core.Option
import arrow.core.getOrNone

/**
 * Extension function to get an Option from a nullable object on a map.
 */
fun <K, A> Map<K, A>.getOption(k: K): Option<A> =
  getOrNone(k)
