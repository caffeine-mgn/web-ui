package pw.binom.web

import org.w3c.dom.Element
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class AbstractComponent<T : Element> : Component<T> {
    protected fun afterConstruct() {
        dom.asDynamic().COMPONENT = this
    }

    private var firstStart = true

    protected fun <R> init(func: suspend () -> R): ReadOnlyProperty<AbstractComponent<T>, R> {
        val delegator = InitVariable<T, R>(func)
        initVars += delegator
        return delegator
    }

    private inner class InitVariable<T : Element, R>(val valueProvider: suspend () -> R) :
        ReadOnlyProperty<AbstractComponent<T>, R> {
        private var value: R? = null
        suspend fun init() {
            value = valueProvider()
        }

        override fun getValue(thisRef: AbstractComponent<T>, property: KProperty<*>): R {
            if (firstStart) {
                throw IllegalStateException("Variable will initialized on first component start")
            }
            return value as R
        }
    }

    private val initVars = ArrayList<InitVariable<T, out Any?>>()

    override suspend fun onStart() {
        if (firstStart) {
            firstStart = false
            onInit()
            initVars.forEach {
                it.init()
            }
        }
    }

    override suspend fun onStop() {
    }

    protected open suspend fun onInit() {
    }
}
