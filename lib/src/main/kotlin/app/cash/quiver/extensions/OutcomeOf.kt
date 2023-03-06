package app.cash.quiver.extensions

import app.cash.quiver.Outcome

typealias OutcomeOf<A> = Outcome<Throwable, A>
