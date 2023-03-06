package app.cash.quiver.extensions

import arrow.core.Nel
import arrow.core.Option

/**
 * Applies a function to a NonEmptyList that can result in an optional NonEmptyList
 */
inline fun <A, B> Nel<A>.mapNotNone(f: (A) -> Option<B>): Option<Nel<B>> =
  Nel.fromList(this.toList().flatMap { a -> f(a).toList() })
