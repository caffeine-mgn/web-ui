package pw.binom.web

import org.w3c.dom.Node
import org.w3c.dom.get

inline fun Node.forEachElement(func: (Node) -> Unit) {
    (0 until childNodes.length).forEach {
        val node = childNodes[it]
        func(node!!)
    }
}