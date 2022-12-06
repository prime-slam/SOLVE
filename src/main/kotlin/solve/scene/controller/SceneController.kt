package solve.scene.controller

import javafx.beans.property.SimpleObjectProperty
import solve.scene.model.*
import tornadofx.*

class SceneController : Controller() {
    val scene = SimpleObjectProperty(Scene(emptyList(), emptyList()))
}