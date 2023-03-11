package solve.main

import solve.DarkTheme
import solve.main.splitpane.SidePanelLocation
import solve.main.splitpane.SidePanelSplitPane
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import solve.sidepanel.content.SidePanelContentView
import solve.sidepanel.SidePanelTabsView
import solve.utils.createPxBox
import tornadofx.*

class MainView : View() {

    companion object {
        private const val SideAndSceneDividerRatio = 0.25
    }

    private val sceneView: SceneView by inject()
    private val sidePanelContentView: SidePanelContentView by inject()



    private lateinit var mainViewSplitPane: SidePanelSplitPane

    private val mainViewBorderPane = borderpane {
        addClass(DarkTheme.scene)
        top<MenuBarView>()
        val splitPaneDividersPositions = listOf(SideAndSceneDividerRatio)
        val splitPaneContainedNodes = listOf(sidePanelContentView.root, sceneView.root)
        mainViewSplitPane = SidePanelSplitPane(
            splitPaneDividersPositions,
            splitPaneContainedNodes,
            SidePanelLocation.Left
        )
        mainViewSplitPane.addStylesheet(MainSplitPaneStyle::class)
        center = mainViewSplitPane
        left<SidePanelTabsView>()
    }

    override val root = mainViewBorderPane

    fun hideSidePanelContent() {
        mainViewSplitPane.hideNode(sidePanelContentView.root)
    }

    fun showSidePanelContent() {
        mainViewSplitPane.showNode(sidePanelContentView.root)
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
