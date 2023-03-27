# Module Quiver Library

<img src="doc-images/quiver-logo-02.svg" class="dark-image">
<img src="doc-images/quiver-logo-01.svg" class="light-image">

Quiver is a library that builds upon [Arrow](https://arrow-kt.io/) to make functional programming in Kotlin even
more accessible & delightful.

This module contains types and functions to provide additional functional programming idioms over and above what is 
available from Arrow.

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
