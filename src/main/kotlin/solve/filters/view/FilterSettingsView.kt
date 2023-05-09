package solve.filters.view

import javafx.scene.Node
import solve.project.model.ProjectFrame
import solve.utils.materialfx.mfxCheckbox
import solve.utils.materialfx.mfxRangeSlider
import tornadofx.*

class FilterSettingsView : View() {
    private val filterSettingsContentNode = borderpane {
        top = hbox {
            buildFilterSettingField("Time period", pane())
        }
    }
    override val root = filterSettingsContentNode

    private fun buildFilterSettingField(name: String, settingNode: Node) = hbox {
        mfxCheckbox()
        label(name)
        add(settingNode)
    }

    private fun getFramesMinTimestamp(frames: List<ProjectFrame>) = frames.minOf { it.timestamp }

    private fun getFramesMaxTimestamp(frames: List<ProjectFrame>) = frames.maxOf { it.timestamp }
}
