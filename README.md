
# Quiver

Quiver is a collection of extension methods and handy functions to make the wonderful functional programming Kotlin library, Arrow, even better.

## Types

### Outcome

`Outcome` is a type that represents three possible states a result can be in: Present, Absent or Failure. Under the hood  
it wraps the type `Either<E, Option<A>>` and supports the common functions that Eithers and Options support such  
as `map`, `flatMap` and `zip`.

There is also a type alias `OutcomeOf<A>` which specialises the error side to a `Throwable` for your convenience.

#### Constructors

There are three primary constructors:

```kotlin  
  
data class Present<A>(val value: A) : Outcome<Nothing, A>  
data class Failure<E>(val error: E) : Outcome<E, Nothing>  
object Absent : Outcome<Nothing, Nothing>  
  
```  

or you can use the extension methods thusly:

```kotlin  
A.present()  
E.failure()  
```  

You can also easily convert an `Either<Option<A>>` to an Outcome using `toOutcome()`

```kotlin  
val outcome = "hi".some().right().toOutcome()  
```  

#### Methods
##### map

Map safely transforms a value in the Outcome. It has no effect on `Absent` or `Failure` instances.

```kotlin  
fun <B> map(f: (A) -> B): Outcome<E, B>   
```  

```kotlin  
Present(1).map { it + 1 }     // Present(2)  
Absent.map { it + 1 }         // Absent  
Failure("bad").map { it + 1 } // Failure("bad")  
```  

##### flatMap

FlatMap allows multiple `Outcome`s to be safely chained together, passing the value from the previous as input into the  
next function that produces an `Outcome`

```kotlin  
fun <A, E, C> Outcome<E, A>.flatMap(f: (A) -> Outcome<E, C>): Outcome<E, C>   
```  

```kotlin  
Present(5).flatMap {  
  if (it < 5) {    Present(it)  } else if (it < 10) {    Absent  } else {    Failure("Value too high")  }}  
```  

##### zip

Zip allows you to combine two or more `Outcome`s easily with a supplied function.

```kotlin  
Present(2).zip(Present(3)) { a, b -> a + b }     // Present(5)  
Present(2).zip(Absent) { a, b -> a + b }         // Absent  
Present(2).zip(Failure("nup")) { a, b -> a + b } // Failure("nup")  
  
```  

##### bind

Bind works the same as `flatMap` but with nicer syntax:

```kotlin  
val outcome = outcome {  
  val a = Present(1).bind()  val b = Present(2).bind()  a + b} // Present(3)  
```  

```kotlin  
val outcome = outcome {  
  val a = Present(1).bind()  val b = Absent.bind()  a + b} // Absent  
```  

```kotlin  
val outcome = outcome {  
  val a = Present(1).bind()  val b = Failure("nup").bind()  a + b} // Failure("nup")  
```  

##### catch

Converts a function that throws an exception (throwable) into an Outcome

```kotlin  
fun <R> catch(f: () -> R): Outcome<Throwable, R>  
```  

```kotlin  
val outcome: Outcome<Throwable, String> = Outcome.catch {  
  val text: String = loadFromDiskOrThrow("blah.txt") // returns string or throws an exception  text}  
```  
##### catchOption

Converts a function that throws an exception (throwable) and returns an Option into an Outcome

```kotlin  
fun <R> catch(f: () -> Option<R>): Outcome<Throwable, R>  
```  

```kotlin  
val outcome: Outcome<Throwable, Customer> = Outcome.catch {  
  val customer: Option<Customer> = maybeLoadCustomerOrThrow() // May or may not return a customer but throws on error  customer}  
```  

##### tap

Performs an effect over the value and preserves the original `Outcome`

```kotlin  
fun <B> tap(f: (A) -> B): Outcome<E, A>  
```  

```kotlin  
"hi".present().tap { println("$it world") } // Present("hi")  
```  

##### flatTap

Performs a flatMap across the supplied function, propagating failures or absence  
but preserving the original present value.

```kotlin  
fun <A, E, B> Outcome<E, A>.flatTap(f: (A) -> Outcome<E, B>): Outcome<E, A>  
```  

```kotlin  
1.present().flatTap { a -> "bad".failure() } // Failure("bad")  
1.present().flatTap { a -> Absent } // Absent  
1.present().flatTap { a -> a + 2 } // Present(1) -- value preserved  
```  

##### tapFailure

Performs an effect over the failure side

```kotlin  
fun <A, B, E> Outcome<E, A>.tapFailure(f: (E) -> B): Outcome<E, A>  
```  

```kotlin  
"bad".failure().tapFailure { logger.error("This has gone badly: $it") } // Failure("bad")  
```  

##### tapAbsent

Performs an effect over the absent side

```kotlin  
fun <A, B, E> Outcome<E, A>.tapAbsent(f: (E) -> B): Outcome<E, A>  
```  

```kotlin  
Absent.tapAbsent { logger.info("Not found!") } // Absent  
```


## Aliases

### ErrorOr

An alias for `Either<Throwable, T>`. It reduces boilerplate when operating on Eithers that can fail with any kind of `Throwable`.

```kotlin
val result: Either<Throwable, String> = database.loadMessage()
```
becomes
```kotlin
val result: ErrorOr<String> = database.loadMessage()
```

#### Methods

##### orThrow
Retrieves the Right hand of an Either, or throws the Left hand error.

### OutcomeOf

An alias for `Outcome<Throwable, T>`, which represents a result that can be a successful value, a successful absence or a failure. It also reduces boilerplate type signatures.
```kotlin
val result: Either<Throwable, Option<String>> = database.loadMaybeMessage()
```
becomes
```kotlin
val result: OutcomeOf<String> = database.loadMaybeMessage()
```


## Extensions

### Eithers

#### or

Given `eitherA.or { eitherB }`, will return the first of the two eithers that is `Right`, or `eitherB` in the case that both are `Left`.

#### asOption

When `Left`, converts to `None`, discarding the value. When `Right`, converts to `Some`, keeping the value.

#### leftAsOption

Like `asOption`, but left-biased.

#### tapLeft

Passes the left value to the provided function and returns the same value unchanged. This is useful for executing effects on the left hand side (such as error logging) without modifying the value.

#### forEach

Executes the provided function if the value is Right. Returns `Unit`

#### leftForEach

Like `forEach`, but left-biased.

#### flatTap

Performs an effect over the right side of the value but maps the original value back into the Either. This is useful for mixing with validation functions.

### Lists

#### filterNotNone

Like `filterNotNull`, but applied to `Option`s

#### listOfSome

A stand-alone method that will convert `vararg Option<T>`s into a `List<T>` with `None`s removed.

#### mapNotNone

Like `mapNotNull`, but applied to functions that return `Option`s.

### Map

#### getOption

Returns an `Option` from the map for the given key. If the key is not present, `None` is returned.

### NonEmptyList

#### mapNotNone

Like `mapNotNull`, but applied to functions that return `Option`s.

### Nullables

#### toEither

Similiar to `A?.toOption`, this will return an Either. The caller must provide a function to supply the left hand side in the case of `null`.

### Option

#### forEach

Runs a side effect if the `Option` is a `Some`.

#### ifAbsent

Like the inverse of `forEach`. Takes a function to run if your `Option` is `None`. Does nothing and returns Unit if the `Option` is `Some`.

#### toValidatedNel

Turns the `Option` into a `Validated` list of `T` if it's a `Some`. If it's a `None`, will return a `Nel` of the error function provided.

### Suspended Functions

#### map

The outcome of a suspended function is mapped into a second suspended fuction, as if the function were a functor.

### Validated

#### ValidatedNel.attemptValidated

Turns a `ValidatedNel` into an `Either`, converting to a `Left<RuntimeException, T>` if there are any validation failures.

#### T.validate

This is shorthand for validating values. Given a predicate and an error-generating function return either the original value in a `ValidNel` if the predicate evaluates as `true` or the generated error in an `InvalidNel`.

```kotlin
"hi mum".validate({it.contains("hi")},{"did not say 'hi'"})
```

#### T.validateEither

Like `validate`, but returns an `Either`. Given a predicate and an error generating function return either the original value in a `Right` if the predicate evaluates as `true` or the error as a `Left`.

#### ValidatedNel.takeLeft

Often you have two validations that return the same thing, and you don't want necessarily to pair them. `takeLeft` will return the value of the left side iff both validations succeed.
```kotlin
Valid("hi").takeLeft(Valid("mum")) == Valid("hi")
```

#### ValidatedNel.takeRight

Like `takeLeft`, but right biased.

```kotlin
Valid("hi").takeRight(Valid("mum")) == Valid("mum")
```

#### T.validateMap

Given a mapping function and an error generating message, return either the result of the function in a  `ValidNel` if the function completes successfully, or the error in an `InvalidNel`.

#### ValidatedNel.concatMap

The `Validated` type doesn't natively support `flatMap` because of the monad laws that it breaks. But this  is what flatMap would do if it were allowed. It is called `concatMap` because the Kotlin compiler will want to wire in the Monad flatMap extension instead and confusion reigns.

```kotlin
 val maybeCustomer: ValidatedNel<String, Customer> = ...  
 val result : ValidatedNel<String, String> = maybeCustomer.concatMap { c ->
     validateName(c.name) // returns a ValidatedNel<String, String>
 }
 result == ValidNel("jack") 
```
