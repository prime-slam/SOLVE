package solve.main

import solve.catalogue.controller.CatalogueController
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import solve.scene.SceneFacade
import solve.scene.controller.SceneController
import solve.utils.*
import tornadofx.*

class MainController : Controller() {
    private val view: MainView by inject()
    private val sceneController: SceneController by inject()
    private val catalogueController: CatalogueController by inject()
    private val sceneFacade = SceneFacade(sceneController).also { ServiceLocator.registerService(it) }

    fun displayCatalogueFrames(frames: List<ProjectFrame>) = catalogueController.setCatalogueFrames(frames)

    fun hideSidePanelContent() {
        view.hideSidePanelContent()
    }

    fun showSidePanelContent() {
        view.showSidePanelContent()
    }

    fun visualizeProject(layers: List<ProjectLayer>, frames: List<ProjectFrame>) = sceneFacade.visualize(layers, frames)
}
