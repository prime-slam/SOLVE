package sliv.tool.controller

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.*

class VisualizationController : Controller() {
    val availableColors: ObservableList<Color> =
        FXCollections.observableArrayList(c("#FF0000"), c("#00FF00"), c("#0000FF"))
    val landmarksColorProperty = SimpleObjectProperty(availableColors.first())
}