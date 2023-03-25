package solve.settings.visualization.popover

import javafx.scene.Node
import solve.scene.controller.SceneController
import solve.scene.model.LayerSettings

class PlaneLayerSettingsPopOverNode(
    private val planeLayerSettings: LayerSettings.PlaneLayerSettings,
    private val sceneController: SceneController
): LayerSettingsPopOverNode() {
    override fun getPopOverNode(): Node {
        TODO("Not yet implemented")
    }
}
