package pw.binom.property

import pw.binom.web.Listener

abstract class AbstractProperty<T> : Property<T> {
    protected abstract fun internalSetValue(value: T)
    protected abstract fun internalGetValue(): T
    override suspend fun getValue(): T = internalGetValue()

    private val listeners = ArrayList<PropertyChangeListener<T>>()
    private val listenersForRemove = ArrayList<PropertyChangeListener<T>>()
    private var listenerLock = false

    protected open suspend fun updateValue(value: T) {
        val old = this.getValue()
        internalSetValue(value)

        listenerLock = true
        try {
            listeners.forEach {
                it.onChange(oldValue = old, newValue = value)
            }
        } finally {
            listenerLock = false
        }
        if (listenersForRemove.isNotEmpty()) {
            listenersForRemove.forEach { listeners -= it }
        }
    }

    override fun addListener(func: PropertyChangeListener<T>): Listener {
        listeners += func
        return Listener {
            if (listenerLock) {
                listenersForRemove += func
            } else {
                listeners -= func
            }
        }
    }
}
