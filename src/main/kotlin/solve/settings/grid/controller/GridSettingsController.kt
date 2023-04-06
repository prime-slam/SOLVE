package solve.settings.grid.controller

import solve.scene.controller.SceneController
import tornadofx.Controller

class GridSettingsController: Controller() {
    private val sceneController: SceneController by inject()

    fun setColumnsNumber(columnsNumber: Int) {
        sceneController.columnsNumber = columnsNumber
    }

    fun setMinScale(minScale: Double) {
        sceneController.minScale = minScale
    }

    fun setMaxScale(maxScale: Double) {
        sceneController.maxScale = maxScale
    }
}
