package solve.main

import io.github.palexdev.materialfx.css.themes.MFXThemeManager
import io.github.palexdev.materialfx.css.themes.Themes
import solve.catalogue.view.CatalogueView
import solve.constants.IconsSettingsGridGridPath
import solve.constants.IconsSidePanelCataloguePath
import solve.constants.IconsSidePanelVisualizationSettingsPath
import solve.main.splitpane.SidePanelLocation
import solve.main.splitpane.SidePanelSplitPane
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import solve.settings.grid.view.GridSettingsView
import solve.settings.visualization.VisualizationSettingsView
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentView
import solve.sidepanel.tabs.SidePanelTabsView
import solve.utils.createPxBox
import solve.utils.loadResourcesImage
import tornadofx.*

class MainView : View() {
    private val sceneView: SceneView by inject()

    private lateinit var mainViewSplitPane: SidePanelSplitPane

    private val leftSidePanelTabs = listOf(SidePanelTab(
        "Catalogue",
        loadResourcesImage(IconsSidePanelCataloguePath),
        find<CatalogueView>().root
    ))
    private val rightSidePanelTabs = listOf(
        SidePanelTab(
            "Layers",
            loadResourcesImage(IconsSidePanelVisualizationSettingsPath),
            find<VisualizationSettingsView>().root
        ),
        SidePanelTab(
            "Grid",
            loadResourcesImage(IconsSettingsGridGridPath),
            find<GridSettingsView>().root
        )
    )

    private val leftSidePanelViews =
        createSidePanelsViews(leftSidePanelTabs, SidePanelLocation.Left, leftSidePanelTabs.first())
    private val rightSidePanelViews =
        createSidePanelsViews(rightSidePanelTabs, SidePanelLocation.Right)


    private val mainViewBorderPane = borderpane {
        top<MenuBarView>()
        val splitPaneDividersPositions = listOf(
            LeftSidePanelAndSceneDividerPosition,
            RightSidePanelAndSceneDividerPosition
        )
        val splitPaneContainedNodes = listOf(
            leftSidePanelViews.contentView.root,
            sceneView.root,
            rightSidePanelViews.contentView.root
        )

        mainViewSplitPane = SidePanelSplitPane(
            splitPaneDividersPositions,
            splitPaneContainedNodes,
            SidePanelLocation.Both,
            SidePanelLocation.Left
        )
        mainViewSplitPane.addStylesheet(MainSplitPaneStyle::class)
        center = mainViewSplitPane
        left = leftSidePanelViews.tabsView.root
        right = rightSidePanelViews.tabsView.root
    }

    override val root = mainViewBorderPane

    override fun onBeforeShow() {
        super.onBeforeShow()
        MFXThemeManager.addOn(root.scene, Themes.DEFAULT)
    }

    fun hideSidePanelContent(location: SidePanelLocation) {
        mainViewSplitPane.hideNodeAt(location)
    }

    fun showSidePanelContent(location: SidePanelLocation) {
        mainViewSplitPane.showNodeAt(location)
    }

    private fun createSidePanelsViews(
        tabs: List<SidePanelTab>,
        location: SidePanelLocation,
        initialTab: SidePanelTab? = null
    ): SidePanelViews {
        val scope = Scope()

        val contentView = find<SidePanelContentView>(scope)

        val tabsViewParams = mapOf(
            TabsViewLocationParamName to location,
            TabsViewTabsParamName to tabs,
            TabsViewInitialTabParamName to initialTab
        )
        val tabsView = find<SidePanelTabsView>(scope, tabsViewParams)

        return SidePanelViews(tabsView, contentView)
    }

    private data class SidePanelViews(val tabsView: SidePanelTabsView, val contentView: SidePanelContentView)

    companion object {
        private const val LeftSidePanelAndSceneDividerPosition = 0.25
        private const val RightSidePanelAndSceneDividerPosition = 0.88

        private const val TabsViewLocationParamName = "location"
        private const val TabsViewTabsParamName = "tabs"
        private const val TabsViewInitialTabParamName = "initialTab"
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
