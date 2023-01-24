package pw.binom.web

object Validators {
    val NOT_BLANK = Validator<String> { it.isNotBlank() }
    val NOT_EMPTY = Validator<String> { it.isNotEmpty() }
    val IS_DOUBLE = Validator<String> { it.toDoubleOrNull() != null }
    val IS_INTEGER = Validator<String> { it.toIntOrNull() != null }
    fun greater(value: Double) = Validator<Double> { it > value }
    fun greaterOrEquals(value: Double) = Validator<Double> { it >= value }
    fun less(value: Double) = Validator<Double> { it < value }
    fun lessOrEquals(value: Double) = Validator<Double> { it <= value }
}

val Validator<Double>.asString
    get() = Validators.IS_DOUBLE and this.map<Double, String> { it.toDouble() }
