package app.cash.quiver.extensions

import arrow.core.Either
import arrow.core.Ior
import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.some
import kotlin.experimental.ExperimentalTypeInference
import app.cash.quiver.extensions.traverse as quiverTraverse

@PublishedApi
internal val unitIor: Ior<Nothing, Unit> = Right(Unit)

inline fun <A, B, C, D> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> =
  zip(
    combine,
    c,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor
  ) { b, cc, _, _, _, _, _, _, _, _ -> map(b, cc) }

inline fun <A, B, C, D, E> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> =
  zip(
    combine,
    c,
    d,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor
  ) { b, cc, dd, _, _, _, _, _, _, _ -> map(b, cc, dd) }

inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> =
  zip(
    combine,
    c,
    d,
    e,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor,
    unitIor
  ) { b, cc, dd, ee, _, _, _, _, _, _ -> map(b, cc, dd, ee) }

inline fun <A, B, C, D, E, F, G> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  map: (B, C, D, E, F) -> G
): Ior<A, G> =
  zip(combine, c, d, e, f, unitIor, unitIor, unitIor, unitIor, unitIor) { b, cc, dd, ee, ff, _, _, _, _, _ ->
    map(
      b,
      cc,
      dd,
      ee,
      ff
    )
  }

inline fun <A, B, C, D, E, F, G, H> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  map: (B, C, D, E, F, G) -> H
): Ior<A, H> =
  zip(combine, c, d, e, f, g, unitIor, unitIor, unitIor, unitIor) { b, cc, dd, ee, ff, gg, _, _, _, _ ->
    map(
      b,
      cc,
      dd,
      ee,
      ff,
      gg
    )
  }

inline fun <A, B, C, D, E, F, G, H, I> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  map: (B, C, D, E, F, G, H) -> I
): Ior<A, I> =
  zip(combine, c, d, e, f, g, h, unitIor, unitIor, unitIor) { b, cc, dd, ee, ff, gg, hh, _, _, _ ->
    map(
      b,
      cc,
      dd,
      ee,
      ff,
      gg,
      hh
    )
  }

inline fun <A, B, C, D, E, F, G, H, I, J> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  map: (B, C, D, E, F, G, H, I) -> J
): Ior<A, J> =
  zip(combine, c, d, e, f, g, h, i, unitIor, unitIor) { b, cc, dd, ee, ff, gg, hh, ii, _, _ ->
    map(
      b,
      cc,
      dd,
      ee,
      ff,
      gg,
      hh,
      ii
    )
  }

inline fun <A, B, C, D, E, F, G, H, I, J, K> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  j: Ior<A, J>,
  map: (B, C, D, E, F, G, H, I, J) -> K
): Ior<A, K> =
  zip(combine, c, d, e, f, g, h, i, j, unitIor) { b, cc, dd, ee, ff, gg, hh, ii, jj, _ ->
    map(
      b,
      cc,
      dd,
      ee,
      ff,
      gg,
      hh,
      ii,
      jj
    )
  }

@Suppress("UNCHECKED_CAST")
inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  j: Ior<A, J>,
  k: Ior<A, K>,
  transform: (B, C, D, E, F, G, H, I, J, K) -> L
): Ior<A, L> {
  val right: Option<L> = if (
    (this@zip.isRight() || this@zip.isBoth()) &&
    (c.isRight() || c.isBoth()) &&
    (d.isRight() || d.isBoth()) &&
    (e.isRight() || e.isBoth()) &&
    (f.isRight() || f.isBoth()) &&
    (g.isRight() || g.isBoth()) &&
    (h.isRight() || h.isBoth()) &&
    (i.isRight() || i.isBoth()) &&
    (j.isRight() || j.isBoth()) &&
    (k.isRight() || k.isBoth())
  ) {
    transform(
      this@zip.getOrNull() as B,
      c.getOrNull() as C,
      d.getOrNull() as D,
      e.getOrNull() as E,
      f.getOrNull() as F,
      g.getOrNull() as G,
      h.getOrNull() as H,
      i.getOrNull() as I,
      j.getOrNull() as J,
      k.getOrNull() as K
    ).some()
  } else None

  var left: Option<A> = None

  if (this@zip is Left) return@zip Left(this@zip.value)
  left = if (this@zip is Both) Some(this@zip.leftValue) else left

  if (c is Left) return@zip Left(left.emptyCombine(c.value, combine))
  left = if (c is Both) Some(left.emptyCombine(c.leftValue, combine)) else left

  if (d is Left) return@zip Left(left.emptyCombine(d.value, combine))
  left = if (d is Both) Some(left.emptyCombine(d.leftValue, combine)) else left

  if (e is Left) return@zip Left(left.emptyCombine(e.value, combine))
  left = if (e is Both) Some(left.emptyCombine(e.leftValue, combine)) else left

  if (f is Left) return@zip Left(left.emptyCombine(f.value, combine))
  left = if (f is Both) Some(left.emptyCombine(f.leftValue, combine)) else left

  if (g is Left) return@zip Left(left.emptyCombine(g.value, combine))
  left = if (g is Both) Some(left.emptyCombine(g.leftValue, combine)) else left

  if (h is Left) return@zip Left(left.emptyCombine(h.value, combine))
  left = if (h is Both) Some(left.emptyCombine(h.leftValue, combine)) else left

  if (i is Left) return@zip Left(left.emptyCombine(i.value, combine))
  left = if (i is Both) Some(left.emptyCombine(i.leftValue, combine)) else left

  if (j is Left) return@zip Left(left.emptyCombine(j.value, combine))
  left = if (j is Both) Some(left.emptyCombine(j.leftValue, combine)) else left

  if (k is Left) return@zip Left(left.emptyCombine(k.value, combine))
  left = if (k is Both)Some(left.emptyCombine(k.leftValue, combine)) else left

  return when(right) {
    is Some -> when(left) {
      is Some -> Both(left.value, right.value)
      is None -> Right(right.value)
    }
    None -> when(left) {
      is Some -> Left(left.value)
      is None -> throw IllegalStateException("Ior.zip should not be possible to reach this state")
    }
  }
}

@PublishedApi
internal inline fun <A> Option<A>.emptyCombine(other: A, combine: (A, A) -> A): A =
  when (this) {
    is Some -> combine(this.value, other)
    is None -> other
  }

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B, AA, C> Ior<A, B>.traverse(f: (B) -> Either<AA, C>): Either<AA, Ior<A, C>> =
  fold(
    { a -> Either.Right(Left(a)) },
    { b -> f(b).map { Right(it) } },
    { a, b -> f(b).map { Both(a, it) } }
  )

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B, AA, C> Ior<A, B>.traverseEither(f: (B) -> Either<AA, C>): Either<AA, Ior<A, C>> =
  quiverTraverse(f)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B, C> Ior<A, B>.traverse(f: (B) -> Option<C>): Option<Ior<A, C>> =
  fold(
    { a -> Some(Left(a)) },
    { b -> f(b).map { Right(it) } },
    { a, b -> f(b).map { Both(a, it) } }
  )

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B, C> Ior<A, B>.traverseOption(f: (B) -> Option<C>): Option<Ior<A, C>> =
  quiverTraverse(f)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <A, B, C> Ior<A, B>.traverse(f: (B) -> Iterable<C>): List<Ior<A, C>> =
  fold(
    { a -> listOf(Left(a)) },
    { b -> f(b).map { Right(it) } },
    { a, b -> f(b).map { Both(a, it) } }
  )
