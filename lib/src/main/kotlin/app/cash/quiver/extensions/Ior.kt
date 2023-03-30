package app.cash.quiver.extensions

import arrow.core.Ior

@PublishedApi
internal val unitIor: Ior<Nothing, Unit> = Ior.Right(Unit)

inline fun <A, B, C, D> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> =
  zip(combine, c, unitIor, unitIor, unitIor, unitIor, unitIor, unitIor, unitIor, unitIor) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

inline fun <A, B, C, D, E> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> =
  zip(combine, c, d, unitIor, unitIor, unitIor, unitIor, unitIor, unitIor, unitIor) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> =
  zip(combine, c, d, e, unitIor, unitIor, unitIor, unitIor, unitIor, unitIor) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

inline fun <A, B, C, D, E, F, G> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  map: (B, C, D, E, F) -> G
): Ior<A, G> =
  zip(combine, c, d, e, f, unitIor, unitIor, unitIor, unitIor, unitIor) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

inline fun <A, B, C, D, E, F, G, H> Ior<A, B>.zip(
  crossinline combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  map: (B, C, D, E, F, G) -> H
): Ior<A, H> =
  zip(combine, c, d, e, f, g, unitIor, unitIor, unitIor, unitIor) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

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
  zip(combine, c, d, e, f, g, h, unitIor, unitIor, unitIor) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

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
  zip(combine, c, d, e, f, g, h, i, unitIor, unitIor) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

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
  zip(combine, c, d, e, f, g, h, i, j, unitIor) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

inline fun <A, B, D> Ior<A, B>.flatMap(combine: (A, A) -> A, f: (B) -> Ior<A, D>): Ior<A, D> =
  when (this) {
    is Ior.Left -> this
    is Ior.Right -> f(value)
    is Ior.Both ->
      f(this@flatMap.rightValue).fold(
        { a -> Ior.Left(combine(this@flatMap.leftValue, a)) },
        { d -> Ior.Both(this@flatMap.leftValue, d) },
        { ll, rr -> Ior.Both(combine(this@flatMap.leftValue, ll), rr) }
      )
  }

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
): Ior<A, L> = flatMap(combine) { a ->
    c.flatMap(combine) { bb ->
      d.flatMap(combine) { cc ->
        e.flatMap(combine) { dd ->
          f.flatMap(combine) { ee ->
            g.flatMap(combine) { ff ->
              h.flatMap(combine) { gg ->
                i.flatMap(combine) { hh ->
                  j.flatMap(combine) { ii ->
                    k.map { jj ->
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
