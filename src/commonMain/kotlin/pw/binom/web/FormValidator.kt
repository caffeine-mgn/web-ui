package pw.binom.web

class FormValidator(val validators: List<Validated>) : Validated {

    private fun calcTotalIsValid() = validators.all { it.isValid }

    private var internalIsValid = calcTotalIsValid()

    override val isValid: Boolean
        get() = internalIsValid

    override val onValidChange = EventElement<Boolean>()

    private val onValidChanged: suspend (Boolean) -> Unit = LISTENER@{ valid ->
        if (internalIsValid == valid) {
            return@LISTENER
        }

        val newValid = calcTotalIsValid()
        if (internalIsValid != newValid) {
            internalIsValid = newValid
            onValidChange.dispatch(newValid)
        }
    }

    init {
        validators.forEach {
            it.onValidChange.on(onValidChanged)
        }
    }
}
