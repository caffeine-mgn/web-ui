package pw.binom.property

fun interface PropertyChangeListener<T> {
    @Suppress("FUN_INTERFACE_WITH_SUSPEND_FUNCTION")
    suspend fun onChange(oldValue: T, newValue: T)
}