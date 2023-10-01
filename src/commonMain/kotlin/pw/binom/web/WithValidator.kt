package pw.binom.web

interface WithValidator<T> : Validated {
    var validator: Validator<T>
}
