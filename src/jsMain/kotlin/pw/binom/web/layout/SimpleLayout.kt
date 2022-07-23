package pw.binom.web.layout

import org.w3c.dom.HTMLElement

open class SimpleLayout<T : HTMLElement>(parent: T) : Layout<T> {
    private var _parent: T

    init {
        val dom_element = parent
        val g = js("dom_element.LAYOUT")
        if (g != null || g != undefined) {
            g.close()
        }

        this._parent = parent

        val self = this
        js("dom_element.LAYOUT=self")
    }

    override val parent: T
        get() = _parent
}
