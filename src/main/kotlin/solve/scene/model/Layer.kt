package solve.scene.model

sealed class Layer(
    val name: String,
    open val settings: LayerSettings,
    open val getLandmarks: () -> List<Landmark>
) {
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

    class PlaneLayer(
        name: String,
        override val settings: LayerSettings.PlaneLayerSettings,
        override val getLandmarks: () -> List<Landmark>
    ) : Layer(name, settings, getLandmarks)
}
