package pw.binom.web

infix fun <T> Validator<T>.or(other: Validator<T>) = Validator<T> {
    this@or.valid(it) || other.valid(it)
}

infix fun <T> Validator<T>.and(other: Validator<T>) = Validator<T> {
    this@and.valid(it) && other.valid(it)
}

fun <T> Validator<T>.invert() = Validator<T> { value -> !this@invert.valid(value) }

fun <F, S> Validator<F>.map(map: (S) -> F) = Validator<S> { value ->
    this@map.valid(map(value))
}
