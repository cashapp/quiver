Change Log
==========

Next Release
----------------------------

**New**
* Add `kotlin.Result.toEither()` - [Jem Mawson][synesso]

**Fixes**
* Fixes assertion error thrown when Outcome is Absent - [Hugo Müller-Downing][hugomd]

Version 0.2.0 *(2023-04-06)*
----------------------------

**New**
* Add Either.traverse - [Simon Vergauwen][nomisRev]
* Either, Nullable & Ior zip - [Simon Vergauwen][nomisRev]
* Adds validateNotNull extension function on nullable values - [Hugo Müller-Downing][hugomd]

**Fixes**
* Fix outcome traverse - [Simon Vergauwen][nomisRev]
* Fix nested nullable issue - [Simon Vergauwen][nomisRev]
* Fix nested null bug (#22) - [Simon Vergauwen][nomisRev]


Version 0.1.0 *(2023-03-06)*
----------------------------

* Add `Option.unit()` and `Either.unit()` to replace the `void()` method deprecated by Arrow.
* Include `withRetries` method on suspended supplier functions to provide opinionated access to Arrow's `Schedule`.
