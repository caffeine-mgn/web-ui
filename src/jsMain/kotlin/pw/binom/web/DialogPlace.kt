package pw.binom.web

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

class DialogPlace : Stage, AbstractComponent<HTMLDivElement>() {
    override val dom: HTMLDivElement = document.createDiv()
}
