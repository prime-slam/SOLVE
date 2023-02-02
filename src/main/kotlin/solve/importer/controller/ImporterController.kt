package solve.importer.controller

import javafx.beans.property.SimpleStringProperty
import solve.importer.model.ImporterProject
import tornadofx.Controller
import tornadofx.objectProperty

class ImporterController : Controller() {
    val directoryPath = SimpleStringProperty()
    val project = objectProperty<ImporterProject>()
}