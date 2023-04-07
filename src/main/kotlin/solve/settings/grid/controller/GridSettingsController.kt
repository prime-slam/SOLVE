package solve.settings.grid.controller

import solve.scene.controller.SceneController
import solve.settings.grid.view.GridSettingsView
import tornadofx.Controller
import tornadofx.onChange

class GridSettingsController: Controller() {
    private val view: GridSettingsView by inject()
    private val sceneController: SceneController by inject()

    init {
        addSceneListeners()
    }

    fun setSceneColumnsNumber(columnsNumber: Int) {
        sceneController.columnsNumber = columnsNumber
    }

    fun setSceneMinScale(minScale: Double) {
        sceneController.scaleLowValue = minScale
    }

    fun setSceneMaxScale(maxScale: Double) {
        sceneController.scaleHighValue = maxScale
    }

    private fun addSceneListeners() {
        sceneController.sceneProperty.onChange { newScene ->
            newScene ?: return@onChange

            val newColumnsNumber = SceneController.calculateColumnsCount(newScene)
            view.columnsCounterValue = newColumnsNumber

            view.setDefaultScaleRangeSliderValues()
        }
    }
}
