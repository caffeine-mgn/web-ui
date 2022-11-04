package pw.binom.web

import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import kotlin.js.Promise

fun Document.getOrCreateHead(): HTMLHeadElement {
    var head = head
    if (head != null) {
        return head
    }
    head = createElement("head").unsafeCast<HTMLHeadElement>()
    appendChild(head)
    return head
}

inline fun Node.forEachElement(func: (Node) -> Unit) {
    (0 until childNodes.length).forEach {
        val node = childNodes[it]
        func(node!!)
    }
}

fun EventTarget.on(event: String, func: (Event) -> Unit): Listener {
    addEventListener(event, func)
    return Listener {
        removeEventListener(event, func)
    }
}

inline fun EventTarget.once(event: String, noinline func: (Event) -> Unit) {
    var c: Listener? = null
    c = on(event) {
        func(it)
        c!!.stopListen()
    }
}

val Float.toDoubleUnsafe
    get() = unsafeCast<Double>()

val Double.toFloatUnsafe
    get() = unsafeCast<Float>()

fun Document.createDiv() = createElement("div").unsafeCast<HTMLDivElement>()
fun Document.createStyle() = createElement("style").unsafeCast<HTMLStyleElement>()
fun Document.createSpan() = createElement("span").unsafeCast<HTMLSpanElement>()
fun Document.createSpan(text: String) = createSpan().also {
    it.innerText = text
}

fun Document.createLink() = createElement("a").unsafeCast<HTMLAnchorElement>()
fun Document.createLink(text: String, href: String) = createLink().also {
    it.innerText = text
    it.href = href
}

fun <T> T.asPromise() = Promise.resolve(this)

inline fun <T> Boolean.ifTrue(func: () -> T): T? =
    if (this) {
        func()
    } else {
        null
    }