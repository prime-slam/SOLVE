package sliv.tool.main

import sliv.tool.scene.SceneFacade
import sliv.tool.scene.controller.SceneController
import tornadofx.*

class MainController : Controller() {
    private val sceneController: SceneController by inject()
    val sceneFacade = SceneFacade(sceneController)
}