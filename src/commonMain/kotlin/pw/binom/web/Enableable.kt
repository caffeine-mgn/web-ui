package pw.binom.web

import pw.binom.property.MutableProperty

interface Enableable {
    val isEnabled: MutableProperty<Boolean>
}
