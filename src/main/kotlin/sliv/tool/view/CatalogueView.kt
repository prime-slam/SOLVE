package sliv.tool.view

import javafx.collections.FXCollections
import javafx.scene.Parent
import tornadofx.*

class CatalogueView : View() {
    override val root = hbox {
        listview(FXCollections.observableArrayList("1", "2", "3"))
    }
}