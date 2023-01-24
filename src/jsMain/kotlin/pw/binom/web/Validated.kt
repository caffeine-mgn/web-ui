package pw.binom.web

interface Validated {
    val isValid: Boolean
    val onValidChange: EventElement<Boolean>
}
