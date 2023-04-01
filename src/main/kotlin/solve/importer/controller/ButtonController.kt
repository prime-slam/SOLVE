package solve.importer.controller

import solve.importer.model.ButtonModel
import tornadofx.Controller
import tornadofx.onChange

class ButtonController : Controller() {
    val model: ButtonModel by inject()

    private val controller: ImporterController by inject()

    fun changeDisable(model: ButtonModel) {
        model.disabled.value = true
        controller.projectAfterPartialParsing.onChange {
            model.disabled.value = it?.hasAnyErrors ?: true
        }
    }
}
