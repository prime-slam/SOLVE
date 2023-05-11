package solve.main

import solve.catalogue.controller.CatalogueController
import solve.main.splitpane.SidePanelLocation
import solve.project.controller.ProjectController
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import solve.scene.SceneFacade
import tornadofx.*

class MainController : Controller() {
    private val view: MainView by inject()
    private val catalogueController: CatalogueController by inject()

    fun hideSidePanelContent(location: SidePanelLocation) {
        view.hideSidePanelContent(location)
    }

    fun showSidePanelContent(location: SidePanelLocation) {
        view.showSidePanelContent(location)
    }

    fun displayCatalogueFrames(frames: List<ProjectFrame>) = catalogueController.setCatalogueFrames(frames)

    fun visualizeProject(layers: List<ProjectLayer>, frames: List<ProjectFrame>) {
        SceneFacade.visualize(layers, frames, false)
    }
}
