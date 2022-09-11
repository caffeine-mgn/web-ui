package pw.binom.web

import org.w3c.dom.Element

interface Page<out T : Element> : Component<T>, Stage {
    suspend fun getTitle(): String?
    suspend fun updateHash(value: String?)
    suspend fun updateParams(params: Map<String, String?>)

    suspend fun next(pageName: String): Page<Element>?
    val face: Face<Element>
}