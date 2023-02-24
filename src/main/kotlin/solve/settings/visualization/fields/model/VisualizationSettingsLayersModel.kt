package solve.settings.visualization.fields.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import solve.scene.model.LayerSettings

class VisualizationSettingsLayersModel {
    private val _layers = FXCollections.observableArrayList<LayerSettings>()
    val layers: ObservableList<LayerSettings> = FXCollections.unmodifiableObservableList(_layers)

    fun reinitializeLayers(newLayers: List<LayerSettings>) {
        _layers.clear()
        _layers.addAll(newLayers)
    }
}
