package sliv.tool.view

import javafx.beans.property.SimpleStringProperty
import javafx.stage.DirectoryChooser
import tornadofx.*


class ChoosingDirectoryView : View() {
    val directoryChooser = DirectoryChooser()
    val path = SimpleStringProperty()

    override val root = hbox(10) {
        label {
            bind(path)
        }
        button("Change"){
            action{
                directoryChooser.title = ("Choose working directory")
                val dir = directoryChooser.showDialog(currentStage)
                path.value = dir.absolutePath

            }
        }
    }





    }

