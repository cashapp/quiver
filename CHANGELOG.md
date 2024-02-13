# Change Log

## [Unreleased]
* Backport traverse functions on Either, Iterable and Option (Andrew Parker)
* Backport traverse functions on Sequence, Map and Ior (Andrew Parker)

## [0.5.0] - 2023-08-26

### Added
* Lazy version of `or`:`Option.or(() -> Option<T>)` (Chris Myers)
* Adds `Option.orEmpty()` (Milly Rowett)
* Backport traverse functions on NonEmptyList (Andrew Parker)

### Changed
* Deprecated `Option.or(Option)` in favour of `Option.or(() -> Option<T>)` (Chris Myers)

## [0.4.0] - 2023-07-26

### Added
* `Option.or(Option)` (Mehdi Mollaverdi)

### Changed
* Bumped Arrow to v1.2.0 from v1.2.0-RC (Jem Mawson)


## [0.3.0] - 2023-06-16

### Added
* `kotlin.Result.toEither()` (Jem Mawson)
* `validateNotNull` extension function on nullable values (Hugo Müller-Downing)


## [0.2.0] - 2023-04-06

### Added
* Add Either.traverse (Simon Vergauwen)
* Either, Nullable & Ior zip (Simon Vergauwen)
* Adds validateNotNull extension function on nullable values (Hugo Müller-Downing)

### Fixed
* Fix outcome traverse (Simon Vergauwen)
* Fix nested nullable issue (Simon Vergauwen)
* Fix nested null bug (#22) (Simon Vergauwen)


## [0.1.0] - 2023-03-06

### Added
* `Option.unit()` and `Either.unit()` to replace the `void()` method deprecated by Arrow.
* `withRetries` method on suspended supplier functions to provide opinionated access to Arrow's `Schedule`.
