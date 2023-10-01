package pw.binom.property

import pw.binom.web.Listener

private class PropertyMapper<T, R>(val parent: Property<T>, val map: suspend (T) -> R) : Property<R> {
    override suspend fun getValue(): R = map(parent.getValue())

    override fun addListener(func: PropertyChangeListener<R>): Listener = parent.addListener { old, new ->
        func.onChange(
            oldValue = map(old),
            newValue = map(new),
        )
    }
}

fun <T, R> Property<T>.map(mapFunction: suspend (T) -> R): Property<R> = PropertyMapper(
    parent = this,
    map = mapFunction,
)

private val constListener = Listener { }

class ConstProperty<T>(private val value: T) : Property<T> {

    override suspend fun getValue(): T = value

    override fun addListener(func: PropertyChangeListener<T>): Listener = constListener
}

fun <T> T.asProperty(): Property<T> = ConstProperty(this)

fun <T : Any> Property<T?>.notNullCondition(msg: String = "Property is null") = map {
    it ?: throw IllegalStateException(msg)
}

suspend fun <T : Any, R> Property<T?>.ifNull(func: suspend () -> R): R? {
    val value = getValue()
    return if (value == null) {
        func()
    } else {
        null
    }
}

suspend fun <T : Any, R> Property<T?>.ifNotNull(func: suspend (T) -> R): R? {
    val value = getValue()
    return if (value != null) {
        func(value)
    } else {
        null
    }
}

fun <T : Any, R : Property<T?>> R.orDefault(value: T): Property<T> = DefaultValueProperty(parent = this, value = value)

private class DefaultValueProperty<T : Any>(val parent: Property<T?>, val value: T) : Property<T> {
    override suspend fun getValue(): T = parent.getValue() ?: value

    override fun addListener(func: PropertyChangeListener<T>): Listener =
        parent.addListener { old, new ->
            func.onChange(oldValue = old ?: value, newValue = new ?: value)
        }
}