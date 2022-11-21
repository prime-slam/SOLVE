package sliv.tool.catalogue.controller

import sliv.tool.catalogue.extractLayers
import sliv.tool.catalogue.model.CatalogueModel
import sliv.tool.project.model.LayerKind
import sliv.tool.project.model.ProjectFrame
import sliv.tool.project.model.ProjectLayer
import sliv.tool.scene.SceneFacade
import sliv.tool.scene.controller.SceneController
import tornadofx.Controller

class CatalogueController: Controller() {
    val model = CatalogueModel()

    private val sceneController: SceneController by inject()
    private val sceneFacade = SceneFacade(sceneController)

    fun displayFrames(frames: List<ProjectFrame>) {
        model.reinitializeFrames(frames)
    }

    fun createFramesSelection(frames: List<ProjectFrame>) {
        val layers = frames.map { it.extractLayers() }.toSet().flatten()
        sceneFacade.visualize(layers, frames)
    }
}