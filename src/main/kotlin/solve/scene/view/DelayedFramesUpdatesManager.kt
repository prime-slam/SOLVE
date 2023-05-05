package solve.scene.view

import solve.scene.model.VisualizationFrame
import solve.utils.DelayedUpdatesManager

object DelayedFramesUpdatesManager : DelayedUpdatesManager<FrameView, VisualizationFrame?>()
