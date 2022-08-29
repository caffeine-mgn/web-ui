package pw.binom.web.layout

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import pw.binom.web.AbstractComponent
import pw.binom.web.Component

class GridLayout<T : HTMLElement>(parent: T) : SimpleLayout<T>(parent), Component<T> {
    override val dom: T
        get() = parent

    init {
        dom.style.display = "grid"
    }

    private var started = false

    override suspend fun onStart() {
        Component.callOnStart(dom)
        started = true
    }

    override suspend fun onStop() {
        started = false
        Component.callOnStop(dom)
    }

    fun column(size: Int): GridLayout<T> {
        val old = dom.style.asDynamic().gridTemplateColumns ?: ""
        dom.style.asDynamic().gridTemplateColumns = "$old ${size}fr".trim()
        return this
    }
}

fun <T : GridComponentLayout> T.column(size: Int): T {
    layout.column(size)
    return this
}

fun <T : HTMLElement, V : Component<T>> V.appendTo(layout: GridLayout<*>): V {
    layout.parent.appendChild(dom)

    return this
}

open class GridComponent : AbstractComponent<HTMLDivElement>() {
    override val dom: HTMLDivElement = document.createElement("div").unsafeCast<HTMLDivElement>()
    protected open val layout = GridLayout(dom)
}

open class GridComponentLayout : GridComponent() {
    public override val layout
        get() = super.layout
}
