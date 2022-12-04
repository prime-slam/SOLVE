package sliv.tool.main

import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.project.model.*
import sliv.tool.scene.SceneFacade
import sliv.tool.scene.controller.SceneController
import sliv.tool.utils.ServiceLocator
import tornadofx.*
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId

class MainController : Controller() {
    private val sceneController: SceneController by inject()
    val sceneFacade = SceneFacade(sceneController).also { ServiceLocator.registerService(it) }
}
