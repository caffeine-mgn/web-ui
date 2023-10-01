package pw.binom.web

import org.w3c.dom.Element

interface Face<out T : Element> : Component<T> {
    suspend fun <T : Element> setPage(path: List<Page<Element>>, page: Page<T>)
    val page: Page<Element>?
}
