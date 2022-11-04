package pw.binom.web

import kotlinx.browser.document
import kotlinx.dom.addClass

private object StageServiceStyles {
    const val STAGE_STYLE="stagePlace"
    init {
        val style= document.createStyle()
        style.innerHTML=""".$STAGE_STYLE{position:fixed;width:100%;height:100%;top:0;left:0;right:0;bottom:0}"""
        document.getOrCreateHead().appendChild(style)
    }
}

abstract class AbstractStageService {
    private val stages = ArrayList<DialogPlace>()
    val stage: Stage?
        get() = stages.lastOrNull() ?: getRootStage()

    protected abstract fun getRootStage():Stage?

    fun getLayout(): DialogPlace {
        val newPlace = DialogPlace()
        document.body!!.appendChild(newPlace.dom)
        newPlace.dom.addClass(StageServiceStyles.STAGE_STYLE)
        stages.add(newPlace)
        return newPlace
    }

    fun dropLayout(dialogPlace: DialogPlace): Boolean {
        if (stages.isEmpty()) {
            return false
        }
        val last = stages.remove(dialogPlace)
        if (!last) {
            return false
        }
        dialogPlace.dom.remove()
        return true
    }
}
