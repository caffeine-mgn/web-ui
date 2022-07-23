package pw.binom.web

class EventElement {

    private val listeners = ArrayList<suspend () -> Unit>()

    fun on(f: suspend () -> Unit): Listener {
        listeners.add(f)
        val closable = Listener {
            listeners.remove(f)
        }

        return closable
    }

    suspend fun on2(f: suspend () -> Unit): Listener {
        val listener = on(f)
        f()
        return listener
    }

    fun once(f: suspend () -> Unit): Listener {
        var e: Listener? = null
        e = on {
            f()
            e!!.stopListen()
        }
        return e
    }

    suspend fun dispatch() {
        for (f in listeners) {
            f()
        }
    }
}
