package sliv.tool.scene.view

import io.github.palexdev.mfxcore.base.beans.Size
import io.github.palexdev.mfxcore.collections.ObservableGrid
import io.github.palexdev.virtualizedfx.enums.ScrollPaneEnums
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import io.github.palexdev.virtualizedfx.utils.*
import sliv.tool.scene.controller.SceneController
import tornadofx.*


class SceneView : View() {
    private val controller: SceneController by inject()

    init {
        controller.scene.onChange { scene ->
            if (scene != null) {
                draw()
            }
        }
    }

    override val root = vbox {
        label("Empty scene placeholder")
    }

    private fun draw() {
        root.children.clear()

        val scene = controller.scene.value
        val frames = (0 until scene.framesCount).map { i -> scene.getFrame.invoke(i)!! }

        //TODO: all cells should have the same size. I can get it from the image of the first frame. What to do if some images have another size (irregular case).
        val width = 500.0
        val height = 500.0

        val data = ObservableGrid.fromList(frames, 30)
        val grid = VirtualGrid(data) { frame -> FrameView(width, height, frame) }
        grid.cellSize = Size(width, height)

        val vsp = VSPUtils.wrap(grid)
        vsp.layoutMode = ScrollPaneEnums.LayoutMode.COMPACT
        vsp.isAutoHideBars = true

        add(vsp)
    }
}
