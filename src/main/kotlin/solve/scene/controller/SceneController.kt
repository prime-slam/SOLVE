package solve.scene.controller

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import solve.scene.model.*
import tornadofx.*

class SceneController : Controller() {
    val scene = SimpleObjectProperty(Scene(emptyList(), emptyList()))
}
