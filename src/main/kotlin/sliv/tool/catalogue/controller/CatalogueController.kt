package sliv.tool.catalogue.controller

import sliv.tool.catalogue.layers
import sliv.tool.catalogue.model.CatalogueModel
import sliv.tool.catalogue.model.ViewFormat
import sliv.tool.catalogue.view.CatalogueSettingsView
import sliv.tool.catalogue.view.CatalogueView
import sliv.tool.project.model.ProjectFrame
import sliv.tool.scene.SceneFacade
import sliv.tool.utils.ServiceLocator
import tornadofx.Controller

class CatalogueController: Controller() {
    companion object {
        val initialViewFormat = ViewFormat.FileName
        val initialSelectionState = CatalogueSettingsView.SelectionState.All
    }

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
        sceneFacade.visualize(layers, frames)
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
}