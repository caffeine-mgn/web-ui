package pw.binom.property

import pw.binom.web.Listener

abstract class AbstractMutableProperty<T> : MutableProperty<T>, AbstractProperty<T>() {
    private var bindedListener: Listener? = null

    protected open suspend fun onValueUpdated(oldValue: T, newValue: T) {}

    private var firstCallAfterBind = false
    private var bindedToProvider: Property<T>? = null

    override suspend fun setValue(value: T) {
        val oldValue = this.internalGetValue()
        unbind()
        updateValue(value)
        onValueUpdated(oldValue = oldValue, newValue = value)
    }

    override fun bind(provider: Property<T>): Listener {
        unbind()
        firstCallAfterBind = true
        val newListener = provider.addListener { old, new ->
            setValue(new)
        }
        bindedToProvider = provider
        bindedListener = newListener
        return Listener {
            newListener.stopListen()
            if (bindedListener === newListener) {
                bindedListener = null
            }
        }
    }

    override fun unbind() {
        val listener = bindedListener
        bindedToProvider = null
        bindedListener = null
        listener?.stopListen()
    }

    override suspend fun getValue(): T {
        val bindedToProvider = bindedToProvider
        if (firstCallAfterBind && bindedToProvider != null) {
            firstCallAfterBind = true
            val newValue = bindedToProvider.getValue()
            updateValue(newValue)
            return newValue
        }
        return super.getValue()
    }
}
