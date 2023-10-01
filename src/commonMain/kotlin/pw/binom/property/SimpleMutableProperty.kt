package pw.binom.property

class SimpleMutableProperty<T>(
    inital: T,
    private val onlyOnChanged: Boolean = false,
    val onValueUpdate: PropertyChangeListener<T>? = null,
) : AbstractMutableProperty<T>() {
    private var value: T = inital
    override fun internalGetValue(): T = value
    override fun internalSetValue(value: T) {
        this.value = value
    }

    override suspend fun onValueUpdated(oldValue: T, newValue: T) {
        if (!onlyOnChanged || oldValue != newValue) {
            onValueUpdate?.onChange(oldValue = oldValue, newValue = newValue)
        }
    }
}
