package solve.scene.model

import java.nio.file.Path

/**
 * Aggregates all landmarks from one layer and frame.
 */
sealed class Layer(
    val name: String,
    open val settings: LayerSettings,
    open val getLandmarks: () -> List<Landmark>
) {
    override fun toString(): String {
        return name
    }

    class PointLayer(
        name: String,
        override val settings: LayerSettings.PointLayerSettings,
        override val getLandmarks: () -> List<Landmark.Keypoint>
    ) : Layer(name, settings, getLandmarks)

    class LineLayer(
        name: String,
        override val settings: LayerSettings.LineLayerSettings,
        override val getLandmarks: () -> List<Landmark.Line>
    ) : Layer(name, settings, getLandmarks)

    // A PlanesLayer contains all frame frames.
    class PlanesLayer(
        name: String,
        override val settings: LayerSettings.PlaneLayerSettings,
        val filePath: Path,
        val layerState: LayerState
    ) : Layer(name, settings, { emptyList() })
}
