package sliv.tool.importer.controller

import javafx.beans.property.SimpleStringProperty
import sliv.tool.project.model.Project
import tornadofx.Controller
import tornadofx.objectProperty

class ImporterController : Controller() {
    val directoryPath = SimpleStringProperty()
    val project = objectProperty<Project>()
}