package solve.settings.visualization.fields.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import solve.scene.model.LayerSettings

class VisualizationSettingsLayersModel {
    val layers: ObservableList<LayerSettings> = FXCollections.observableArrayList()

    fun reinitializeLayers(newLayers: List<LayerSettings>) {
        layers.clear()
        layers.addAll(newLayers)
    }
}
