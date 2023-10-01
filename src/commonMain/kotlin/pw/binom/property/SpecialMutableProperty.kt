package pw.binom.property

class SpecialMutableProperty<T>(
    private val getter: () -> T,
    private val setter: (T) -> Unit,
) : AbstractMutableProperty<T>() {

    override fun internalSetValue(value: T) {
        setter(value)
    }

    override fun internalGetValue(): T = getter()
}
