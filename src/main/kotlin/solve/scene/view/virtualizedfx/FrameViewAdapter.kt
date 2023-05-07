package solve.scene.view.virtualizedfx

import io.github.palexdev.virtualizedfx.cell.GridCell
import javafx.scene.Node
import solve.scene.model.VisualizationFrame
import solve.scene.view.FrameView

/**
 * Wraps FrameView with GridCell element to show it inside VirtualizedFX grid.
 */
class FrameViewAdapter(private val view: FrameView) : GridCell<VisualizationFrame?> {
    override fun getNode(): Node {
        return view
    }

    override fun updateItem(frame: VisualizationFrame?) {
        view.setFrame(frame)
    }

    override fun dispose() {
        view.dispose()
        super.dispose()
    }
}
