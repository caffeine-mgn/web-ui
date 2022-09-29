package pw.binom.web

import kotlinx.coroutines.Job
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AutoCancelableJob : ReadWriteProperty<Any, Job?> {
    var value: Job? = null
    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Job?) {
        if (this.value === value) {
            return
        }

        this.value?.cancel()
        this.value = value
    }
}
