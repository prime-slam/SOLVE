package sliv.tool.main

import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.project.model.ProjectFrame
import sliv.tool.scene.SceneFacade
import sliv.tool.scene.controller.SceneController
import sliv.tool.utils.ServiceLocator
import tornadofx.*

class MainController : Controller() {
    private val sceneController: SceneController by inject()
    private val catalogueController: CatalogueController by inject()
    val sceneFacade = SceneFacade(sceneController).also { ServiceLocator.registerService(it) }

    fun displayCatalogueFrames(frames: List<ProjectFrame>) = catalogueController.setCatalogueFrames(frames)
}
