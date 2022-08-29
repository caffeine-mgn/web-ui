package pw.binom.web

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import pw.binom.web.layout.FlexLayout

class FlexDiv2 : AbstractComponent<HTMLDivElement>() {
    override val dom: HTMLDivElement = document.createElement("div").unsafeCast<HTMLDivElement>()

    init {
        afterConstruct()
    }

    val layout = FlexLayout(dom).asParent()
}
