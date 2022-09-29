package pw.binom.web

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import kotlin.coroutines.suspendCoroutine

abstract class AbstractPage<T : Element> : Page<T>, AbstractComponent<T>() {
    override suspend fun getTitle(): String? = null
    private var params: Map<String, String?> = emptyMap()
    private var hash: String? = null
    private var shouldCallParamUpdate = true
    private var shouldCallArgumentsUpdate = true

    override suspend fun updateHash(value: String?) {
        this.hash = value
        if (isStarted) {
            callArgumentsUpdate(value)
        } else {
            shouldCallArgumentsUpdate = true
        }
    }

    override suspend fun updateParams(params: Map<String, String?>) {
        this.params = params
        if (isStarted) {
            callParamUpdate(params)
        } else {
            shouldCallParamUpdate = true
        }
    }

    private val paramUpdateListeners = ArrayList<suspend (Map<String, String?>) -> Unit>()
    private val argumentUpdateListeners = ArrayList<suspend (String?) -> Unit>()

    private suspend fun callParamUpdate(map: Map<String, String?>) {
        paramUpdateListeners.forEach {
            it(map)
        }
    }

    private var oldHash: String? = null
    private var oldHashUpdateJob: Job? = null
    private fun callArgumentsUpdate(hash: String?): Job? {
        if (oldHash == hash) {
            return null
        }
        oldHash = hash
        oldHashUpdateJob?.cancel()
        val j = GlobalScope.launch {
            try {
                argumentUpdateListeners.forEach {
                    it(hash)
                }
            } finally {
                oldHashUpdateJob = null
            }
        }
        oldHashUpdateJob = j
        return j
    }

    protected fun onArgumentApply(func: suspend (String?) -> Unit): PageParamListener {
        argumentUpdateListeners += func
        return PageParamListener {
            func(hash)
        }
    }

    protected fun onParamApply(func: suspend (Map<String, String?>) -> Unit): PageParamListener {
        paramUpdateListeners += func
        return PageParamListener {
            func(params)
        }
    }

    protected fun onParam(name: String, func: suspend (String?) -> Unit) = onParamApply { map ->
        func(map[name])
    }

    private val nextPages = ArrayList<suspend (String) -> Page<Element>?>()

    protected fun defineNext(name: String, next: suspend () -> Page<Element>) {
        defineNext { pageName ->
            if (pageName == name) {
                next()
            } else {
                null
            }
        }
    }

    protected fun defineNext(next: suspend (String) -> Page<Element>?) {
        nextPages += next
    }

    override suspend fun next(pageName: String): Page<Element>? {
        nextPages.forEach {
            val nextPage = it(pageName)
            if (nextPage != null) {
                return nextPage
            }
        }
        return null
    }

    override suspend fun onStart() {
        super.onStart()
        if (shouldCallParamUpdate) {
            shouldCallParamUpdate = false
            callParamUpdate(params)
        }
        if (shouldCallArgumentsUpdate) {
            shouldCallArgumentsUpdate = false
            callArgumentsUpdate(hash)?.join()
        }
    }
}