package pw.binom.web

fun interface Validator<T> {
    companion object {
        fun <T> valid() = Validator<T> { true }
        fun <T> invalid() = Validator<T> { false }
    }

    fun valid(value: T): Boolean
}
