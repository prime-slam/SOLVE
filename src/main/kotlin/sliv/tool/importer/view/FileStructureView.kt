package sliv.tool.importer.view


import javafx.beans.property.SimpleStringProperty
import sliv.tool.importer.controller.ImporterController
import tornadofx.Fragment
import tornadofx.vbox
import tornadofx.*



class FileStructureView : Fragment() {
    private val controller: ImporterController by inject()

    var path = SimpleStringProperty()
    override val root = vbox {
        path.bind(controller.directoryPath) // here I was trying to figure out how binding works. But I haven't figured it out yet
    }

}
