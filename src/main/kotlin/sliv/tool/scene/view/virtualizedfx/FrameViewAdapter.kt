package sliv.tool.scene.view.virtualizedfx

import io.github.palexdev.virtualizedfx.cell.GridCell
import javafx.scene.Node
import sliv.tool.scene.model.VisualizationFrame
import sliv.tool.scene.view.FrameView

class FrameViewAdapter(private val view: FrameView) : GridCell<VisualizationFrame?> {
    override fun getNode(): Node {
        return view
    }

    override fun updateItem(frame: VisualizationFrame?) {
        view.setFrame(frame)
    }

    override fun dispose() {
        println("disposed")
        super.dispose()
    }
}