package solve.sidepanel.tabs

import solve.main.splitpane.SidePanelLocation
import solve.settings.visualization.VisualizationSettingsView
import solve.sidepanel.SidePanelTab
import solve.utils.loadImage
import tornadofx.FX.Companion.find

private val rightSidePanelTabs = listOf(
    SidePanelTab(
    "Visualization",
        loadImage("icons/visualization_settings_icon.png"),
        find<VisualizationSettingsView>().root
    )
)
class RightSidePanelTabsView: SidePanelTabsView(rightSidePanelTabs, SidePanelLocation.Right) {
    init {
        initializeTabs()
    }
}