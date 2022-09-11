package pw.binom.web

import kotlinx.browser.window
import org.w3c.dom.Element
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class AbstractComponent<T : Element> : Component<T> {
    protected fun afterConstruct() {
        Component.setComponent(dom, this)
    }

    protected var isStarted = false
        private set
    private var firstStart = true

    protected fun <R> init(func: suspend () -> R): ReadOnlyProperty<AbstractComponent<T>, R> {
        val delegator = InitVariable<T, R>(func)
        initFunc += { delegator.init() }
        return delegator
    }

    protected fun <T : Component<*>> T.asParent(): T {
        this@AbstractComponent.onStart {
            this@asParent.onStart()
        }
        this@AbstractComponent.onStop {
            this@asParent.onStop()
        }
        return this
    }

    protected fun onInit(func: suspend () -> Unit) {
        initFunc += func
    }

    protected fun onStart(func: suspend () -> Unit) {
        startFunc += func
    }

    protected fun onStop(func: suspend () -> Unit) {
        stopFunc += func
    }

    private inner class InitVariable<T : Element, R>(val valueProvider: suspend () -> R) :
        ReadOnlyProperty<AbstractComponent<T>, R> {
        private var value: R? = null
        suspend fun init() {
            value = valueProvider()
        }

        override fun getValue(thisRef: AbstractComponent<T>, property: KProperty<*>): R {
            if (firstStart) {
                throw IllegalStateException("Variable ${thisRef::class}::\"${property.name}\" will initialized on first component start")
            }
            return value as R
        }
    }

    private val initFunc = ArrayList<suspend () -> Unit>()
    private val startFunc = ArrayList<suspend () -> Unit>()
    private val stopFunc = ArrayList<suspend () -> Unit>()

    override suspend fun onStart() {
        isStarted = true
        if (firstStart) {
            firstStart = false
            onInit()
            initFunc.forEach {
                it()
            }
        }
        startFunc.forEach {
            it()
        }
    }

    override suspend fun onStop() {
        stopFunc.forEach {
            it()
        }
        isStarted = false
    }

    protected open suspend fun onInit() {
    }

    /**
     * Calls [func] and return result only if [isStarted] is `true`. Elso returns `null`
     * @param func function for call only this component started
     * @return result of [func] if component started. Also is `null`
     */
    protected inline fun <T> ifStarted(func: () -> T): T? =
        if (isStarted) {
            func()
        } else {
            null
        }
}
