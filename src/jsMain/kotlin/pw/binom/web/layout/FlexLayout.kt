package pw.binom.web.layout

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.get
import pw.binom.web.Component

class FlexLayout<T : HTMLElement>(
    parent: T,
    direction: Direction = Direction.Row,
    alignItems: AlignItems = AlignItems.Stretch,
    justifyContent: JustifyContent = JustifyContent.Start,
) : SimpleLayout<T>(parent), Component<T> {

    constructor(
        controller: Component<T>,
        direction: Direction = Direction.Row,
        alignItems: AlignItems = AlignItems.Stretch,
        justifyContent: JustifyContent = JustifyContent.Start,
    ) : this(
        parent = controller.dom,
        direction = direction,
        alignItems = alignItems,
        justifyContent = justifyContent,
    )

    private var started = false
    val isStarted
        get() = started

    override suspend fun onStart() {
        started = true
        Component.callOnStart(dom)
    }

    override suspend fun onStop() {
        started = false
        Component.callOnStop(dom)
    }

    override val dom: T
        get() = parent

    companion object {
//        fun layout(
//            direction: Direction = Direction.Row,
//            listener: (FlexLayout<HTMLDivElement>.() -> Unit)? = null
//        ): FlexComponent {
//            val com = FlexComponent()
//            com.layout.direction = direction
//            if (listener !== null)
//                com.layout.listener()
//
//            return com
//        }

        fun div(listener: (FlexLayout<HTMLDivElement>.() -> Unit)? = null): FlexLayout<HTMLDivElement> {
            val element = document.createElement("div").unsafeCast<HTMLDivElement>()
//            val element = FlexDiv2()
            val g = FlexLayout(element)
            Component.setComponent(element, g)
            if (listener != null) {
                g.listener()
            }
            return g
        }
    }

    fun clear() {
        while (parent.hasChildNodes()) {
            val el = parent.firstChild!!
            parent.removeChild(parent.firstChild!!)
            js("delete el.FLEX_ITEM")
        }
    }

    private fun refreshFlexAttr() {
        if (_inline) {
            parent.style.display = "inline-flex"
        } else {
            parent.style.display = "flex"
        }
    }

    private var _inline: Boolean = false
    var inline: Boolean
        get() = _inline
        set(it) {
            _inline = it
            refreshFlexAttr()
        }

    private var _direction: Direction = Direction.Row
    private var _justifyContent: JustifyContent = JustifyContent.Start

    var justifyContent: JustifyContent
        get() = _justifyContent
        set(it) {
            _justifyContent = it
            parent.style.justifyContent = it.css
        }

    private var _alignItems: AlignItems = AlignItems.Stretch
    private var _wrap: Wrap = Wrap.Disable
    var alignItems: AlignItems
        get() = _alignItems
        set(it) {
            _alignItems = it
            parent.style.alignItems = it.css
        }

    var wrap: Wrap
        get() = _wrap
        set(it) {
            _wrap = it
            parent.style.flexWrap = it.css
        }

    private var _alignContent: AlignContent = AlignContent.Stretch
    var alignContent: AlignContent
        get() = _alignContent
        set(it) {
            _alignContent = it
            parent.style.alignContent = it.css
        }

    var direction: Direction
        get() = _direction
        set(it) {
            _direction = it
            parent.style.flexDirection = it.css
        }

    init {
        inline = inline
        this.direction = direction
        this.alignItems = alignItems
        alignContent = alignContent
        this.justifyContent = justifyContent
        refreshFlexAttr()
        parent.childNodes.length
        for (f in 0..parent.childNodes.length - 1) {
            val element = parent.childNodes.get(f)
            if (element is HTMLElement) {
                val item = FlexItem(element)
                js("element.FLEX_ITEM=item")
            }
        }
    }

    fun item(element: HTMLElement): FlexItem {
        if (element.parentNode !== parent) {
            throw RuntimeException("Element not in layout")
        }
        return js("element.FLEX_ITEM")
    }

    private fun prepareElement(element: Element, control: (FlexItem.() -> Unit)?) {
        val item = FlexItem(element)
        element.asDynamic().FLEX_ITEM = item
        if (control != null) {
            item.control()
        }
    }

    fun <T : HTMLElement> addFirst(element: T, control: (FlexItem.() -> Unit)? = null): T =
        if (parent.childElementCount == 0) {
            add(element = element, control = control)
        } else {
            addBefore(element = element, before = parent.children.get(0) as HTMLElement, control = control)
        }

    suspend fun <T : HTMLElement> addFirstAndStart(element: T, control: (FlexItem.() -> Unit)? = null): T =
        if (parent.childElementCount == 0) {
            addAndStart(element = element, control = control)
        } else {
            addBeforeAndStart(element = element, before = parent.children.get(0) as HTMLElement, control = control)
        }

    private suspend fun callStart(element: Element) {
        val com = element.asDynamic().COMPONENT ?: return
        if (com !is Component<*>) return
        com.onStart()
    }

    private suspend fun callStop(element: Element) {
        val com = element.asDynamic().COMPONENT ?: return
        if (com !is Component<*>) return
        com.onStop()
    }

    fun <T : Element> add(element: T, control: (FlexItem.() -> Unit)? = null): T {
        prepareElement(element, control)
        parent.appendChild(element)
        return element
    }

    suspend fun <T : Element> addAndStart(element: T, control: (FlexItem.() -> Unit)? = null): T {
        add(element = element, control = control)
        if (started) {
            callStart(element)
        }
        return element
    }

    fun <T : HTMLElement> addBefore(
        element: T,
        before: HTMLElement,
        control: (FlexItem.() -> Unit)? = null,
    ): T {
        prepareElement(element, control)
        parent.insertBefore(node = element, child = before)
        return element
    }

    suspend fun <T : HTMLElement> addBeforeAndStart(
        element: T,
        before: HTMLElement,
        control: (FlexItem.() -> Unit)? = null,
    ): T {
        addBefore(
            element = element,
            before = before,
            control = control,
        )
        if (started) {
            callStart(element)
        }
        return element
    }

    fun <T : HTMLElement> addAfter(element: T, after: HTMLElement, control: (FlexItem.() -> Unit)? = null): T {
        prepareElement(element, control)
        parent.insertBefore(node = element, child = after.nextSibling)
        return element
    }

    suspend fun <T : HTMLElement> addAfterAndStart(
        element: T,
        after: HTMLElement,
        control: (FlexItem.() -> Unit)? = null,
    ): T {
        addAfter(
            element = element,
            after = after,
            control = control,
        )
        if (started) {
            callStart(element)
        }
        return element
    }

    fun <T : HTMLElement> remove(element: T): T {
        val el = element
        item(element).diatach()
        parent.removeChild(element)
        js("delete el.FLEX_ITEM")
        return element
    }

    suspend fun <T : HTMLElement> removeAndStop(element: T): T {
        if (started) {
            callStop(element)
        }
        remove(element)
        return element
    }

    enum class Direction(val css: String) {
        Row("row"), RowReverse("row-reverse"), Column("column"), ColumnReverse("column-reverse")
    }

    enum class JustifyContent(val css: String) {
        Start("flex-start"), End("flex-end"), Center("center"), SpaceBetween("space-between"), SpaceAround("space-around")
    }

    enum class AlignItems(val css: String) {
        Start("flex-start"), End("flex-end"), Center("center"), Baseline("baseline"), Stretch("stretch")
    }

    enum class Wrap(val css: String) {
        Disable("nowrap"), Enable("wrap"), EnableReverse("wrap-reverse")
    }

    enum class AlignContent(val css: String) {
        Start("flex-start"), End("flex-end"), Center("center"), SpaceBetween("space-between"), SpaceAround("space-around"), Stretch(
            "stretch",
        )
    }

    class FlexItem {
        private var dom: Element? = null

        private var _flexShrink: Int = 1

        var shrink: Int
            get() = _flexShrink
            set(it) {
                _flexShrink = it
                dom?.style?.flexShrink = it.toString()
            }

        private var _flexBasis: Int? = null
        var basis: Int?
            get() = _flexBasis
            set(it) {
                _flexBasis = it
                this.dom?.style?.flexBasis = if (it == null) {
                    "auto"
                } else if (it <= 0) "${-it}%" else "${it}px"
            }
        private var _flexGrow: Int = 0
        var grow: Int
            get() = _flexGrow
            set(it) {
                dom?.style?.flexGrow = it.toString()
                _flexGrow = it
            }

        private var _alignSelf: AlignSelf = AlignSelf.Auto

        var align: AlignSelf
            get() = _alignSelf
            set(it) {
                _alignSelf = it
                if (dom != null) {
                    dom!!.style.alignSelf = it.css
                }
            }

        var order: Int?
            get() = dom?.style?.order?.toIntOrNull()
            set(value) {
                dom?.style?.order = value?.toString() ?: ""
            }

        constructor(dom: Element) {
            this.dom = dom
            align = align
            shrink = shrink
            grow = grow
            basis = basis
        }

        fun diatach() {
            dom!!.style.removeProperty("flex")
            dom!!.style.removeProperty("align-self")
        }

        enum class AlignSelf(val css: String) {
            Auto("auto"), Start("flex-start"), End("flex-end"), Center("center"), Baseline("baseline"), Stretch("stretch"), Normal(
                "normal",
            )
        }
    }
}

private val Element.style: CSSStyleDeclaration
    get() = this.asDynamic().style
