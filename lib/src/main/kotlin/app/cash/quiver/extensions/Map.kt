package app.cash.quiver.extensions

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

/**
 * Extension function to get an Option from a nullable object on a map.
 */
@Suppress("UNCHECKED_CAST")
fun <K, A> Map<K, A>.getOption(k: K): Option<A> =
  if (containsKey(k)) Some(get(k) as A) else None
