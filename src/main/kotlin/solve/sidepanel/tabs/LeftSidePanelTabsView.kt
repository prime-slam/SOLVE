package solve.sidepanel.tabs

import solve.catalogue.view.CatalogueView
import solve.main.splitpane.SidePanelLocation
import solve.sidepanel.SidePanelTab
import solve.utils.loadImage
import tornadofx.FX.Companion.find

private val leftSidePanelTabs = listOf(
    SidePanelTab(
        "Catalogue",
        loadImage("icons/sidepanel_catalogue_icon.png"),
        find<CatalogueView>().root
    )
)

class LeftSidePanelTabsView: SidePanelTabsView(leftSidePanelTabs, SidePanelLocation.Left) {
    init {
        initializeTabs()
    }
}