package solve.catalogue.controller

import solve.catalogue.layers
import solve.catalogue.model.CatalogueModel
import solve.catalogue.model.ViewFormat
import solve.catalogue.view.CatalogueSettingsView
import solve.catalogue.view.CatalogueView
import solve.project.model.ProjectFrame
import solve.scene.SceneFacade
import solve.utils.ServiceLocator
import tornadofx.Controller

class CatalogueController: Controller() {
    val model = CatalogueModel()

    private val view: CatalogueView by inject()
    private val settingsView: CatalogueSettingsView by inject()

    fun setCatalogueFrames(frames: List<ProjectFrame>) {
        model.reinitializeFrames(frames)
        reinitializeCatalogue()
    }

    fun visualizeFramesSelection(frames: List<ProjectFrame>) {
        val sceneFacade = ServiceLocator.getService<SceneFacade>() ?: return

        val layers = frames.flatMap { it.layers }.distinct()
        sceneFacade.visualize(layers, frames, true)
    }

    fun selectAllFields() = view.selectAllFields()

    fun deselectAllFields() = view.deselectAllFields()

    fun changeViewFormat(format: ViewFormat) = view.changeViewFormat(format)

    fun onFieldsSelectionChanged() {
        settingsView.updateCheckBoxView()
    }

    fun displayInfoLabel(withText: String) = settingsView.displayInfoLabel(withText)

    fun hideInfoLabel() = settingsView.hideInfoLabel()

    private fun reinitializeCatalogue() {
        settingsView.resetSettings()
        view.reinitializeView()
    }

    companion object {
        val initialViewFormat = ViewFormat.FileName
        val initialSelectionState = CatalogueSettingsView.SelectionState.All
    }
}
