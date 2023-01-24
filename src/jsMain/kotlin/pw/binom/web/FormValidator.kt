package pw.binom.web

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FormValidator(val validators: List<Validated>) : Validated {

    private fun calcTotalIsValid() = validators.all { it.isValid }

    private var internalIsValid = calcTotalIsValid()

    override val isValid: Boolean
        get() = internalIsValid

    override val onValidChange = EventElement<Boolean>()

    init {
        console.info("FormValidator:: Initing...")
        validators.forEachIndexed { index, validated ->
            console.info("FormValidator:: $index ->${validated.isValid} (${validated::class.js.name})")
        }
        console.info("FormValidator:: isValid: $isValid")
    }

    private val onValidChanged: (Boolean) -> Unit = LISTENER@{ valid ->
        if (internalIsValid == valid) {
            return@LISTENER
        }

        val newValid = calcTotalIsValid()
        if (internalIsValid != newValid) {
            internalIsValid = newValid
            GlobalScope.launch { onValidChange.dispatch(newValid) }
        }
    }

    init {
        validators.forEach {
            it.onValidChange.on(onValidChanged)
        }
    }
}
