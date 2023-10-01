package pw.binom.web

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.w3c.dom.Element
import pw.binom.property.MutableProperty
import pw.binom.property.SimpleMutableProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class AbstractComponent<T : Element> : Component<T> {
    protected fun afterConstruct() {
        Component.setComponent(dom, this)
        dom.setAttribute("data-status", "unknown")
    }

    protected var isStarted = false
        private set
    private var firstStart = true

    protected fun <T> T.config(func: suspend T.() -> Unit): T {
        initFunc += { func(this) }
        return this
    }

    private inner class AsyncProperty<T>(val func: suspend () -> T, val destroyOnStop: Boolean) :
        ReadOnlyProperty<AbstractComponent<*>, Deferred<T>> {
        private var value: Deferred<T>? = null

        init {
            onStop {
                if (destroyOnStop) {
                    value?.cancel()
                    value = null
                }
            }
        }

        override fun getValue(thisRef: AbstractComponent<*>, property: KProperty<*>): Deferred<T> {
            var job = value
            if (job == null) {
                job = GlobalScope.async {
                    func()
                }
                value = job
                return job
            }
            return job
        }
    }

    protected fun <T> asyncValue(
        caching: Boolean = false,
        func: suspend () -> T,
    ): ReadOnlyProperty<AbstractComponent<*>, Deferred<T>> = AsyncProperty(
        func = func,
        destroyOnStop = !caching,
    )

    private inner class DefComponent<T : Component<*>?>(val func: suspend () -> T) :
        ReadOnlyProperty<AbstractComponent<*>, T> {
        private var component: T? = null

        init {
            onStart {
                component = func()
            }
            onStop {
                component?.dom?.remove()
            }
        }

        override fun getValue(thisRef: AbstractComponent<*>, property: KProperty<*>): T {
            return if (isStarted) {
                component as T
            } else {
                throw IllegalStateException("Can't get component \"${property.name}\". Component not started")
            }
        }
    }

    protected fun <T : Component<*>?> define(func: suspend () -> T): ReadOnlyProperty<AbstractComponent<*>, T> =
        DefComponent(func)

    protected fun <T> dependsOnProperty(inital: T): MutableProperty<T> = SimpleMutableProperty(
        inital = inital,
        onlyOnChanged = true,
        onValueUpdate = { _, _ ->
            if (isStarted) {
                onStop()
                onStart()
            }
        },
    )

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
        dom.setAttribute("data-status", "started")
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
        dom.setAttribute("data-status", "stopped")
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

    protected fun job(): AutoCancelableJob {
        val j = AutoCancelableJob()
        onStop {
            j.value?.cancel()
            j.value = null
        }
        return j
    }
}
