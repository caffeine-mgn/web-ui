package pw.binom.web

fun <T, V : WithValidator<T>> V.validator(validator: Validator<T>): V {
    this.validator = validator
    return this
}
