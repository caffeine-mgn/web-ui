package pw.binom.web

operator fun Validated.plus(other: Validated): Validated =
    when {
        this is FormValidator && other is FormValidator -> FormValidator(this.validators + other.validators)
        this is FormValidator -> FormValidator(this.validators + listOf(other))
        other is FormValidator -> FormValidator(listOf(other) + other.validators)
        else -> FormValidator(listOf(this, other))
    }

fun Validated.invert() = object : Validated {
    override val isValid: Boolean
        get() = !this@invert.isValid
    override val onValidChange = EventElement<Boolean>()

    init {
        this@invert.onValidChange.on { valid -> onValidChange.dispatch(!valid) }
    }
}
