package solve.settings.grid.controller

import solve.scene.controller.SceneController
import tornadofx.Controller

class GridSettingsController : Controller() {
    private val sceneController: SceneController by inject()

    fun setSceneColumnsNumber(columnsNumber: Int) {
        sceneController.installedColumnsNumber = columnsNumber
    }

    fun setSceneMinScale(minScale: Double) {
        sceneController.installedMinScale = minScale
    }

    fun setSceneMaxScale(maxScale: Double) {
        sceneController.installedMaxScale = maxScale
    }
}
