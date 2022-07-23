package pw.binom.web

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

interface Component<out T : Element> {
    companion object {
        suspend fun callOnStart(element: HTMLElement) {
            (0 until element.childNodes.length).forEach {
                val node = element.childNodes[it]
                val component = node.asDynamic().COMPONENT.unsafeCast<Component<*>?>() ?: return@forEach
                component.onStart()
            }
        }

        suspend fun callOnStop(element: HTMLElement) {
            (0 until element.childNodes.length).forEach {
                val node = element.childNodes[it]
                val component = node.asDynamic().COMPONENT.unsafeCast<Component<*>?>() ?: return@forEach
                component.onStop()
            }
        }
    }

    val dom: T
    suspend fun onStart()
    suspend fun onStop()
}
