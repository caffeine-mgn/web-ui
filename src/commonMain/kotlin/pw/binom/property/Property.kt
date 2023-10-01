package pw.binom.property

import pw.binom.web.Listener

interface Property<T> {
    suspend fun getValue(): T
    fun addListener(func: PropertyChangeListener<T>): Listener
}
