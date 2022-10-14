package sliv.tool.model

import tornadofx.*
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

class Visualization {
    val keypoints =  (0 .. 200).map { i -> Keypoint((i + 50).toDouble(), (i + 50).toDouble()) }

    val keypointColorProperty = SimpleObjectProperty<Color>()
}

class VisualizationModel(property: ObjectProperty<Visualization>) : ItemViewModel<Visualization>(itemProperty = property) {
    val selectedColor = bind(autocommit = true) {
        item?.keypointColorProperty
    }
}