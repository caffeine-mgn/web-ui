package pw.binom.property

import pw.binom.web.Listener

interface MutableProperty<T> : Property<T> {
    suspend fun setValue(value: T)
    fun bind(provider: Property<T>): Listener
    fun unbind()
}

fun <R, T : MutableProperty<R>> T.alsoBind(property: Property<R>): T {
    bind(property)
    return this
}
