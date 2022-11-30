package sliv.tool.catalogue.controller

import sliv.tool.catalogue.layers
import sliv.tool.catalogue.model.CatalogueModel
import sliv.tool.project.model.ProjectFrame
import sliv.tool.scene.SceneFacade
import sliv.tool.utils.ServiceLocator
import tornadofx.Controller

class CatalogueController: Controller() {
    val model = CatalogueModel()

    fun setCatalogueFrames(frames: List<ProjectFrame>) {
        model.reinitializeFrames(frames)
    }

    fun visualizeFramesSelection(frames: List<ProjectFrame>) {
        val sceneFacade = ServiceLocator.getService<SceneFacade>() ?: return

        val layers = frames.flatMap { it.layers }.distinct()
        sceneFacade.visualize(layers, frames)
    }
}