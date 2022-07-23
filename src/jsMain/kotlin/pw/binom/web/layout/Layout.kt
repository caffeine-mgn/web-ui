package pw.binom.web.layout

import org.w3c.dom.HTMLElement

interface Layout<T : HTMLElement> {
    val parent: T
}