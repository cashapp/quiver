package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.toOption

/**
 * Retrieves the Right hand of an Either, or throws the Left hand error
 */
fun <A> Either<Throwable, A>.orThrow() = this.getOrElse { t -> throw t }

/**
 * Turns a nullable value into an [Either]. This is useful for building validation functions.
 *
 * @return [Either.Left] if the value is null, [Either.Right] if the value is not null.
 * @param label Optional [String] to identify the nullable value being evaluated, used in the failure message.
 */
fun <B> B?.validateNotNull(label: Option<String> = None): Either<Throwable, B> = this.toEither {
  IllegalArgumentException("Value${label.map { " (`$it`)" }.getOrElse { "" }} should not be null")
}

/**
 * Returns the first successful either, otherwise the last failure
 */
inline fun <E, A> Either<E, A>.or(f: () -> Either<E, A>): Either<E, A> = when (this) {
  is Either.Left -> f()
  is Either.Right -> this
}

/**
 * Turns your Either into an Option.
 */
fun <E, A> Either<E, A>.asOption(): Option<A> = when (this) {
  is Either.Left -> None
  is Either.Right -> Some(this.value)
}

/**
 * Turns the left side of your Either into an Option.
 */
fun <E, A> Either<E, A>.leftAsOption(): Option<E> = when (this) {
  is Either.Left -> Some(this.value)
  is Either.Right -> None
}

/**
 * Pass the left value to the function (e.g. for logging errors)
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B> Either<A, B>.tapLeft(f: (A) -> Unit): Either<A, B> = this.mapLeft {
  f(it)
  it
}

/**
 * Performs an effect on the right side of the Either.
 */
inline fun <A, B> Either<A, B>.forEach(f: (B) -> Unit): Unit = when (this) {
  is Either.Left -> Unit
  is Either.Right -> f(this.value)
}

/**
 * Performs an effect on the left side of the Either.
 */
inline fun <A, B> Either<A, B>.leftForEach(f: (A) -> Unit): Unit = when (this) {
  is Either.Left -> f(this.value)
  is Either.Right -> Unit
}

/**
 * Performs an effect over the right side of the value but maps the original value back into
 * the Either.  This is useful for mixing with validation functions.
 */
inline fun <A, B, C> Either<A, B>.flatTap(f: (B) -> Either<A, C>): Either<A, B> = this.flatMap { b ->
  f(b).map { b }
}

/**
 * Lifts a nullable value into an Either, similar to toOption.  Must supply the left side
 * of the Either.
 */
inline fun <A, B> B?.toEither(left: () -> A): Either<A, B> = this.toOption().toEither { left() }

/**
 * Map on a nested Either Option type.
 */
inline fun <E, T, V> Either<E, Option<T>>.mapOption(f: (T) -> V): Either<E, Option<V>> = this.map { it.map(f) }

/**
 * Map right to Unit. This restores `.void()` which was deprecated by Arrow.
 */
fun <A, B> Either<A, B>.unit() = map { }

@PublishedApi
internal val rightUnit: Either<Nothing, Unit> = Either.Right(Unit)

inline fun <A, B, C, D> Either<A, B>.zip(b: Either<A, C>, transform: (B, C) -> D): Either<A, D> =
  flatMap { a ->
    b.map { bb -> transform(a, bb) }
  }

inline fun <A, B, C, D, E> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  transform: (B, C, D) -> E,
): Either<A, E> =
  zip(
    b,
    c,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit
  ) { a, bb, cc, _, _, _, _, _, _, _ -> transform(a, bb, cc) }

inline fun <A, B, C, D, E, F> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  transform: (B, C, D, E) -> F,
): Either<A, F> =
  zip(
    b,
    c,
    d,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit
  ) { a, bb, cc, dd, _, _, _, _, _, _ -> transform(a, bb, cc, dd) }

inline fun <A, B, C, D, E, F, G> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  e: Either<A, F>,
  transform: (B, C, D, E, F) -> G,
): Either<A, G> =
  zip(
    b,
    c,
    d,
    e,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit,
    rightUnit
  ) { a, bb, cc, dd, ee, _, _, _, _, _ -> transform(a, bb, cc, dd, ee) }

inline fun <A, B, C, D, E, F, G, H> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  e: Either<A, F>,
  f: Either<A, G>,
  transform: (B, C, D, E, F, G) -> H,
): Either<A, H> =
  zip(b, c, d, e, f, rightUnit, rightUnit, rightUnit, rightUnit) { a, bb, cc, dd, ee, ff, _, _, _, _ ->
    transform(
      a,
      bb,
      cc,
      dd,
      ee,
      ff
    )
  }

inline fun <A, B, C, D, E, F, G, H, I> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  e: Either<A, F>,
  f: Either<A, G>,
  g: Either<A, H>,
  transform: (B, C, D, E, F, G, H) -> I,
): Either<A, I> =
  zip(b, c, d, e, f, g, rightUnit, rightUnit, rightUnit) { a, bb, cc, dd, ee, ff, gg, _, _, _ ->
    transform(
      a,
      bb,
      cc,
      dd,
      ee,
      ff,
      gg
    )
  }

inline fun <A, B, C, D, E, F, G, H, I, J> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  e: Either<A, F>,
  f: Either<A, G>,
  g: Either<A, H>,
  h: Either<A, I>,
  transform: (B, C, D, E, F, G, H, I) -> J,
): Either<A, J> =
  zip(b, c, d, e, f, g, h, rightUnit, rightUnit) { a, bb, cc, dd, ee, ff, gg, hh, _, _ -> transform(a, bb, cc, dd, ee, ff, gg, hh) }

inline fun <A, B, C, D, E, F, G, H, I, J, K> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  e: Either<A, F>,
  f: Either<A, G>,
  g: Either<A, H>,
  h: Either<A, I>,
  i: Either<A, J>,
  transform: (B, C, D, E, F, G, H, I, J) -> K,
): Either<A, K> =
  zip(b, c, d, e, f, g, h, i, rightUnit) { a, bb, cc, dd, ee, ff, gg, hh, ii, _ -> transform(a, bb, cc, dd, ee, ff, gg, hh, ii) }

inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Either<A, B>.zip(
  b: Either<A, C>,
  c: Either<A, D>,
  d: Either<A, E>,
  e: Either<A, F>,
  f: Either<A, G>,
  g: Either<A, H>,
  h: Either<A, I>,
  i: Either<A, J>,
  j: Either<A, K>,
  transform: (B, C, D, E, F, G, H, I, J, K) -> L,
): Either<A, L> =
  flatMap { a ->
    b.flatMap { bb ->
      c.flatMap { cc ->
        d.flatMap { dd ->
          e.flatMap { ee ->
            f.flatMap { ff ->
              g.flatMap { gg ->
                h.flatMap { hh ->
                  i.flatMap { ii ->
                    j.map { jj ->
                      transform(a, bb, cc, dd, ee, ff, gg, hh, ii, jj)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
