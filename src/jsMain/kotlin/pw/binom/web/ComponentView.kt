package pw.binom.web

import org.w3c.dom.Element

interface ComponentView<E : Component<Element>, out T : Element> : Component<T> {
    val component: E?

    suspend fun set(component: E)
}
