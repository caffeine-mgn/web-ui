package pw.binom.web

class EventElement<T> {

    private val listeners = ArrayList<suspend (T) -> Unit>()

    fun on(f: suspend (T) -> Unit): Listener {
        listeners.add(f)
        val closable = Listener {
            listeners.remove(f)
        }

        return closable
    }

    suspend fun on(value: T, f: suspend (T) -> Unit): Listener {
        f(value)
        val listener = on(f)
        return listener
    }

    fun once(f: suspend (T) -> Unit): Listener {
        var e: Listener? = null
        e = on {
            f(it)
            e!!.stopListen()
        }
        return e
    }

    suspend fun dispatch(value: T) {
        for (f in listeners) {
            f(value)
        }
    }
}
