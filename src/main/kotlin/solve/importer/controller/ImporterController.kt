package solve.importer.controller

import javafx.beans.property.SimpleStringProperty
import solve.importer.model.ProjectAfterPartialParsing
import solve.project.model.Project
import tornadofx.Controller
import tornadofx.objectProperty

class ImporterController : Controller() {
    val directoryPath = SimpleStringProperty()
    val project = objectProperty<Project>()
    val projectAfterPartialParsing = objectProperty<ProjectAfterPartialParsing>()
}