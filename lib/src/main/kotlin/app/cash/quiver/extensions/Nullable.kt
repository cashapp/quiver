package app.cash.quiver.extensions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmStatic

@OptIn(ExperimentalContracts::class)
object Nullable {

  @JvmStatic
  inline fun <A, B, R> zip(a: A?, b: B?, transform: (A, B) -> R): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, Unit) { aa, bb, _ -> transform(aa, bb) }
  }

  @JvmStatic
  inline fun <A, B, C, R> zip(a: A?, b: B?, c: C?, transform: (A, B, C) -> R): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, Unit) { aa, bb, cc, _ -> transform(aa, bb, cc) }
  }

  @JvmStatic
  inline fun <A, B, C, D, R> zip(a: A?, b: B?, c: C?, d: D?, transform: (A, B, C, D) -> R): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, Unit) { aa, bb, cc, dd, _ -> transform(aa, bb, cc, dd) }
  }

  @JvmStatic
  inline fun <A, B, C, D, E, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, transform: (A, B, C, D, E) -> R): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, Unit) { aa, bb, cc, dd, ee, _ -> transform(aa, bb, cc, dd, ee) }
  }

  @JvmStatic
  inline fun <A, B, C, D, E, F, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, transform: (A, B, C, D, E, F) -> R
  ): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, Unit) { aa, bb, cc, dd, ee, ff, _ -> transform(aa, bb, cc, dd, ee, ff) }
  }

  @JvmStatic
  inline fun <A, B, C, D, E, F, G, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, transform: (A, B, C, D, E, F, G) -> R
  ): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, g, Unit) { aa, bb, cc, dd, ee, ff, gg, _ -> transform(aa, bb, cc, dd, ee, ff, gg) }
  }

  @JvmStatic
  inline fun <A, B, C, D, E, F, G, H, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, transform: (A, B, C, D, E, F, G, H) -> R
  ): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, g, h, Unit) { aa, bb, cc, dd, ee, ff, gg, hh, _ ->
      transform(
        aa,
        bb,
        cc,
        dd,
        ee,
        ff,
        gg,
        hh
      )
    }
  }

  @JvmStatic
  inline fun <A, B, C, D, E, F, G, H, I, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, i: I?, transform: (A, B, C, D, E, F, G, H, I) -> R
  ): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, g, h, i, Unit) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
      transform(
        aa,
        bb,
        cc,
        dd,
        ee,
        ff,
        gg,
        hh,
        ii
      )
    }
  }

  @JvmStatic
  inline fun <A, B, C, D, E, F, G, H, I, J, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, i: I?, j: J?, transform: (A, B, C, D, E, F, G, H, I, J) -> R
  ): R? {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return a?.let { aa ->
      b?.let { bb ->
        c?.let { cc ->
          d?.let { dd ->
            e?.let { ee ->
              f?.let { ff ->
                g?.let { gg ->
                  h?.let { hh ->
                    i?.let { ii ->
                      j?.let { jj ->
                        transform(aa, bb, cc, dd, ee, ff, gg, hh, ii, jj)
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
  }
}
