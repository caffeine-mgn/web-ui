package pw.binom.property

class SimpleProperty<T>(
    initial: T,
    private val onlyOnChanged: Boolean = false,
) : AbstractProperty<T>() {
    private var value: T = initial
    override fun internalGetValue(): T = value
    override fun internalSetValue(value: T) {
        this.value = value
    }

    public override suspend fun updateValue(value: T) {
        if (!onlyOnChanged || this.value != value) {
            super.updateValue(value)
        }
    }
}
