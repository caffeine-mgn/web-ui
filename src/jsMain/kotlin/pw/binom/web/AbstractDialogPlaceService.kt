package pw.binom.web

import org.w3c.dom.Element
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class AbstractDialogPlaceService {
    protected abstract val stageService: AbstractStageService

    protected open suspend fun <T> beforeDialogShow(dialog: Dialog<T, out Element>) {
        // Do nothing
    }

    protected open suspend fun <T> beforeDialogHide(dialog: Dialog<T, out Element>) {
        // Do nothing
    }

    private val dialogs = HashMap<Dialog<*, out Element>, Pair<DialogPlace, Continuation<*>>>()

    suspend fun <T> show(dialog: Dialog<T, out Element>): T {
        if (dialogs.containsKey(dialog)) {
            throw IllegalStateException("Dialog already shows")
        }
        val layout = stageService.getLayout()
        beforeDialogShow(dialog)
        layout.dom.appendChild(dialog.dom)
        dialog.onStart()
        return suspendCoroutine {
            dialogs[dialog] = layout to it
        }
    }

    suspend fun <T> close(dialog: Dialog<T, out Element>, value: T) {
        val con = dialogs.remove(dialog) ?: return
        dialog.onStop()
        beforeDialogHide(dialog)
        dialog.dom.remove()
        stageService.dropLayout(con.first)
        (con.second as Continuation<T>).resume(value)
    }
}
