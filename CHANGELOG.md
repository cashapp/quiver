# Change Log

## [Unreleased]

### Added
* Adds `ErrorOr.toResult()` as a shorthand for converting `Either<Throwable, T>` to `Result<T>` (Jem Mawson)

## [0.5.2] - 2024-04-08

### Changed
Upgraded Arrow to v1.2.4

## [0.5.1] - 2024-03-26
### Added
* Adds `Option.ifPresent()` as alternative to `Option.forEach()` for symmetry with `Option.ifAbsent()` (Mia Balogh)

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
