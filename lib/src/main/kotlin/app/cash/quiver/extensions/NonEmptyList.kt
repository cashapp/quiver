package app.cash.quiver.extensions

import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toNonEmptyListOrNull
import arrow.core.toOption

/**
 * Applies a function to a NonEmptyList that can result in an optional NonEmptyList
 */
inline fun <A, B> Nel<A>.mapNotNone(f: (A) -> Option<B>): Option<Nel<B>> =
  this.toList().flatMap { a -> f(a).toList() }.toNonEmptyListOrNull().toOption()
