package solve.project.controller

import solve.main.MainController
import solve.project.model.Project
import solve.project.model.ProjectModel
import tornadofx.*

class ProjectController : Controller() {
    val model = ProjectModel()

    private val mainController: MainController by inject()

    init {
        addMainControllerBindings()
    }

    fun changeProject(newProject: Project) {
        model.changeProject(newProject)
    }

    private fun addMainControllerBindings() {
        model.projectProperty.onChange { newProject ->
            newProject ?: return@onChange

            mainController.visualizeProject(newProject.layers, newProject.frames)
            mainController.displayCatalogueFrames(newProject.frames)
        }
    }
}
