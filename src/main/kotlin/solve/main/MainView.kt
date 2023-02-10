package solve.main

import solve.catalogue.view.CatalogueView
import solve.main.splitpane.SidePanelLocation
import solve.main.splitpane.SidePanelSplitPane
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import solve.settings.visualization.VisualizationSettingsView
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentView
import solve.sidepanel.tabs.SidePanelTabsView
import solve.utils.createPxBox
import solve.utils.loadImage
import tornadofx.*

class MainView : View() {
    companion object {
        private const val LeftSidePanelAndSceneDividerPosition = 0.25
        private const val RightSidePanelAndSceneDividerPosition = 0.75
    }

    private val sceneView: SceneView by inject()

    private lateinit var mainViewSplitPane: SidePanelSplitPane

    private val leftSidePanelScope = Scope()
    private val rightSidePanelScope = Scope()

    private val leftSidePanelContentView: SidePanelContentView by inject(leftSidePanelScope)
    private val rightSidePanelContentView: SidePanelContentView by inject(rightSidePanelScope)

    private val leftSidePanelTabs = listOf(SidePanelTab(
        "Catalogue",
        loadImage("icons/sidepanel_catalogue_icon.png"),
        find<CatalogueView>().root
    ))
    private val rightSidePanelTabs = listOf(SidePanelTab(
        "Visualization",
        loadImage("icons/sidepanel_visualization_settings_icon.png"),
        find<VisualizationSettingsView>().root
    ))
    private val tabsViewLocationParamName = "location"
    private val tabsViewTabsParamName = "tabs"

    private val leftSidePanelTabsViewParams =
        mapOf(tabsViewLocationParamName to SidePanelLocation.Left, tabsViewTabsParamName to leftSidePanelTabs)
    private val rightSidePanelTabsViewParams =
        mapOf(tabsViewLocationParamName to SidePanelLocation.Right, tabsViewTabsParamName to rightSidePanelTabs)
    private val leftSidePanelTabsView: SidePanelTabsView by inject(leftSidePanelScope, leftSidePanelTabsViewParams)
    private val rightSidePanelTabsView: SidePanelTabsView by inject(rightSidePanelScope, rightSidePanelTabsViewParams)


    private val mainViewBorderPane = borderpane {
        top<MenuBarView>()
        val splitPaneDividersPositions = listOf(
            LeftSidePanelAndSceneDividerPosition,
            RightSidePanelAndSceneDividerPosition
        )
        val splitPaneContainedNodes = listOf(
            leftSidePanelContentView.root,
            sceneView.root,
            rightSidePanelContentView.root
        )

        mainViewSplitPane = SidePanelSplitPane(
            splitPaneDividersPositions,
            splitPaneContainedNodes,
            SidePanelLocation.Both
        )
        mainViewSplitPane.addStylesheet(MainSplitPaneStyle::class)
        center = mainViewSplitPane
        left = leftSidePanelTabsView.root
        right = rightSidePanelTabsView.root
    }

    override val root = mainViewBorderPane

    fun hideSidePanelContent(location: SidePanelLocation) {
        mainViewSplitPane.hideNodeAt(location)
    }

    fun showSidePanelContent(location: SidePanelLocation) {
        mainViewSplitPane.showNodeAt(location)
    }
}

class MainSplitPaneStyle: Stylesheet() {
    init {
        splitPane {
            splitPaneDivider {
                padding = createPxBox(0.0, 1.0, 0.0, 1.0)
            }
        }
    }
}
