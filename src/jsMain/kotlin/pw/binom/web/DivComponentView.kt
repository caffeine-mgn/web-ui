package pw.binom.web


import kotlinx.browser.document
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadElement
import pw.binom.web.AbstractComponent
import pw.binom.web.Component



object ViewStyles {
    const val COMPONENT_VIEW = "componentView"
    const val COMPONENT_VIEW_CONTENT="componentViewContent"
    init {
        val style= document.createStyle()
        style.innerHTML=""".$COMPONENT_VIEW{position:relative}.$COMPONENT_VIEW_CONTENT{width:100%;height:100%}"""
        document.getOrCreateHead().appendChild(style)
    }
}

class DivComponentView<T : Component<Element>> : AbstractComponent<HTMLDivElement>(), ComponentView<T, HTMLDivElement> {
    override val dom: HTMLDivElement = document.createElement("div").unsafeCast<HTMLDivElement>()

    init {
        afterConstruct()
        dom.addClass(ViewStyles.COMPONENT_VIEW)
    }

    override var component: T? = null
        private set

    override suspend fun set(component: T) {
        if (this.component === component) {
            return
        }
        this.component?.also {
            //            it.dom.removeClass(FULL_VIEW)
            it.onStop()
            it.dom.removeClass(ViewStyles.COMPONENT_VIEW_CONTENT)
            it.dom.remove()
        }
        this.component = component
        //        component.dom.addClass(FULL_VIEW)
        component.dom.addClass(ViewStyles.COMPONENT_VIEW_CONTENT)
        dom.appendChild(component.dom)
        if (started) {
            component.onStart()
        }
    }

    private var started = false

    override suspend fun onStart() {
        started = true
        component?.onStart()
    }

    override suspend fun onStop() {
        try {
            component?.onStop()
        } finally {
            started = false
        }
    }
}
