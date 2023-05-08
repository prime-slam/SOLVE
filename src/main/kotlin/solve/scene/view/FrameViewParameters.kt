package solve.scene.view

import kotlinx.coroutines.CoroutineScope
import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.OrderManager
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.AssociationsManager

/**
 * Parameters of FrameView which can not be shared between different scenes and should be updated on scene changes.
 */
data class FrameViewParameters(
    val coroutineScope: CoroutineScope,
    val associationsManager: AssociationsManager<VisualizationFrame, Landmark.Keypoint>,
    val orderManager: OrderManager<LayerSettings>
)
