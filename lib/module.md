# Module Quiver Library

![](/doc-images/quiver-logo-01.svg)

Quiver is a collection of extension methods and handy functions to make the wonderful functional programming Kotlin library, Arrow, even better.


# Package app.cash.quiver
Custom types (e.g. [`Outcome`](app.cash.quiver.Outcome))

# Package app.cash.quiver.continuations
Continuations for working with custom types (e.g. [`Outcome`](app.cash.quiver.Outcome))

```kotlin
val outcome1: Outcome<String, Int> = Present(1)
val outcome2: Outcome<String, Int> = Present(2)
outcome {
  val a = outcome1.bind()
  val b = outcome2.bind()
  a + b
}
```

# Package app.cash.quiver.extensions
Extensions of built-in types (e.g. `Either`, `Option`, etc)
