package solve.scene.view

import solve.scene.model.VisualizationFrame

/**
 * Represents all data that change to one FrameView, including cache reusing and virtualization.
 */
data class FrameViewData(val frame: VisualizationFrame?, val frameViewParameters: FrameViewParameters)
