package app.cash.quiver.extensions

fun <A, B> (suspend () -> A).map(f: (A) -> B): suspend () -> B = suspend {
  f(this.invoke())
}

fun <A, B, C> (suspend (A) -> B).map(f: (B) -> C): suspend (A) -> C = { a: A ->
  f(this.invoke(a))
}
