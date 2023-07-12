Change Log
==========

Next Release
----------------------------

**Updates**
* Bumped Arrow to v1.2.0 from v1.2.0-RC <Jem Mawson>

Version 0.3.0 *(2023-06-16)*
----------------------------

**New**
* Add `kotlin.Result.toEither()` <Jem Mawson>

**Fixes**
* Adds validateNotNull extension function on nullable values <Hugo Müller-Downing>

Version 0.2.0 *(2023-04-06)*
----------------------------

**New**
* Add Either.traverse <Simon Vergauwen>
* Either, Nullable & Ior zip <Simon Vergauwen>
* Adds validateNotNull extension function on nullable values <Hugo Müller-Downing>

**Fixes**
* Fix outcome traverse <Simon Vergauwen>
* Fix nested nullable issue <Simon Vergauwen>
* Fix nested null bug (#22) <Simon Vergauwen>


Version 0.1.0 *(2023-03-06)*
----------------------------

* Add `Option.unit()` and `Either.unit()` to replace the `void()` method deprecated by Arrow.
* Include `withRetries` method on suspended supplier functions to provide opinionated access to Arrow's `Schedule`.
