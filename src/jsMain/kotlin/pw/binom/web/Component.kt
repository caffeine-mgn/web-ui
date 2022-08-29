package pw.binom.web

import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

interface Component<out T : Element> {
    companion object {
        fun getComponent(element: HTMLElement) = element.asDynamic().COMPONENT?.unsafeCast<Component<Element>>()
        fun setComponent(element: Element, component: Component<out Element>) {
            element.asDynamic().COMPONENT = component
            element.setAttribute("data-component", component::class.js.name)
        }

        suspend fun callOnStart(element: HTMLElement) {
            element.forEachElement { node ->
                val component = getComponent(node.unsafeCast<HTMLElement>()) ?: return@forEachElement
                component.onStart()
            }
        }

        suspend fun callOnStop(element: HTMLElement) {
            element.forEachElement { node ->
                val component = getComponent(node.unsafeCast<HTMLElement>()) ?: return@forEachElement
                component.onStop()
            }
        }
    }

    val dom: T
    suspend fun onStart()
    suspend fun onStop()
}

fun <T : Component<out HTMLElement>> T.addClass(className: String): T {
    dom.addClass(className)
    return this
}

fun <T : Component<out HTMLElement>> T.removeClass(className: String): T {
    dom.removeClass(className)
    return this
}

fun <T : Component<out HTMLElement>> T.addClass(vararg className: String): T {
    dom.addClass(*className)
    return this
}

fun <T : Component<out HTMLElement>> T.removeClass(vararg className: String): T {
    dom.removeClass(*className)
    return this
}
