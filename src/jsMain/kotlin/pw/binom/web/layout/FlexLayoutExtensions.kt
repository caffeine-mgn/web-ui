package pw.binom.web.layout

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import pw.binom.web.Component

fun <T : HTMLElement, V : Component<T>> V.appendTo(
    layout: FlexLayout<*>,
    grow: Int = 1,
    shrink: Int = 1,
    basis: Int? = null,
    order: Int? = null,
    align: FlexLayout.FlexItem.AlignSelf = FlexLayout.FlexItem.AlignSelf.Auto
): V {
    layout.add(this.dom) {
        this.basis = basis
        this.grow = grow
        this.shrink = shrink
        this.align = align
        this.order = order
    }
    return this
}

suspend fun <T : HTMLElement, V : Component<T>> V.appendToAndStart(
    layout: FlexLayout<*>,
    grow: Int = 1,
    shrink: Int = 1,
    basis: Int? = null,
    order: Int? = null,
    align: FlexLayout.FlexItem.AlignSelf = FlexLayout.FlexItem.AlignSelf.Auto
): V {
    layout.addAndStart(this.dom) {
        this.basis = basis
        this.grow = grow
        this.shrink = shrink
        this.align = align
        this.order = order
    }
    return this
}

fun <T : HTMLElement> T.appendTo(
    layout: FlexLayout<*>,
    grow: Int = 1,
    shrink: Int = 1,
    basis: Int? = null,
    align: FlexLayout.FlexItem.AlignSelf = FlexLayout.FlexItem.AlignSelf.Auto,
    order: Int? = null
): T {
    layout.add(this) {
        this.basis = basis
        this.grow = grow
        this.shrink = shrink
        this.align = align
        this.order = order
    }
    return this
}

fun <T : HTMLElement> T.appendToAndStart(
    layout: FlexLayout<*>,
    grow: Int = 1,
    shrink: Int = 1,
    basis: Int? = null,
    align: FlexLayout.FlexItem.AlignSelf = FlexLayout.FlexItem.AlignSelf.Auto,
    order: Int? = null
): T {
    layout.add(this) {
        this.basis = basis
        this.grow = grow
        this.shrink = shrink
        this.align = align
        this.order = order
    }

    return this
}

fun <T : HTMLElement, V : Component<T>> V.appendFirstTo(
    layout: FlexLayout<*>,
    grow: Int = 1,
    shrink: Int = 1,
    basis: Int? = null,
    order: Int? = null,
    align: FlexLayout.FlexItem.AlignSelf = FlexLayout.FlexItem.AlignSelf.Auto
): V {
    layout.addFirst(this.dom) {
        this.basis = basis
        this.grow = grow
        this.shrink = shrink
        this.align = align
        this.order = order
    }

    return this
}

suspend fun <T : HTMLElement, V : Component<T>> V.appendFirstToAndStart(
    layout: FlexLayout<*>,
    grow: Int = 1,
    shrink: Int = 1,
    basis: Int? = null,
    order: Int? = null,
    align: FlexLayout.FlexItem.AlignSelf = FlexLayout.FlexItem.AlignSelf.Auto
): V {
    layout.addFirstAndStart(this.dom) {
        this.basis = basis
        this.grow = grow
        this.shrink = shrink
        this.align = align
        this.order = order
    }

    return this
}

inline fun <T : Node, V : Node> T.appendTo(node: V): T {
    node.appendChild(this)
    return this
}
