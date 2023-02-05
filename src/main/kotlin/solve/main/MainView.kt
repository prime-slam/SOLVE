package solve.main

import solve.main.splitpane.SidePanelLocation
import solve.main.splitpane.SidePanelSplitPane
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import solve.sidepanel.content.SidePanelContentView
import solve.sidepanel.tabs.LeftSidePanelTabsView
import solve.sidepanel.tabs.RightSidePanelTabsView
import solve.utils.createPxBox
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
    private val leftSidePanelTabsView: LeftSidePanelTabsView by inject(leftSidePanelScope)
    private val rightSidePanelTabsView: RightSidePanelTabsView by inject(rightSidePanelScope)

    private val leftSidePanelContentView: SidePanelContentView by inject(leftSidePanelScope)
    private val rightSidePanelContentView: SidePanelContentView by inject(rightSidePanelScope)

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
        center = mainViewSplitPane.also { println(1) }
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
