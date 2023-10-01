package pw.binom.web

import org.w3c.dom.Element

interface Dialog<T, E : Element> : Component<E> {
    suspend fun show(): T
}
