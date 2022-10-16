package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import tornadofx.*

class CatalogueView : View() {
    override val root = hbox {
        listview(FXCollections.observableArrayList("1", "2", "3"))
    }
}