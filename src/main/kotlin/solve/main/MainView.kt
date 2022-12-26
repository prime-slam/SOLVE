package solve.main

import javafx.scene.control.SplitPane
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import solve.sidepanel.content.SidePanelContentView
import solve.sidepanel.SidePanelTabsView
import solve.utils.createPxBox
import tornadofx.*

class MainView : View() {
    companion object {
        private const val SideAndSceneDividerPosition = 0.25
    }

    private val sceneView: SceneView by inject()
    private val sidePanelContentView: SidePanelContentView by inject()

    private lateinit var mainSplitPane: SplitPane
    private var lastSideAndSceneDividerPosition = SideAndSceneDividerPosition

    override val root = borderpane {
        top<MenuBarView>()
        mainSplitPane = splitpane {
            addStylesheet(MainSplitPaneStyle::class)
            add(sidePanelContentView)
            add(sceneView)
            SplitPane.setResizableWithParent(sidePanelContentView.root, false)
            SplitPane.setResizableWithParent(sceneView.root, false)
            setDividerPosition(0, SideAndSceneDividerPosition)
        }
        center = mainSplitPane
        left<SidePanelTabsView>()
    }

    fun hideSidePanelContent() {
        lastSideAndSceneDividerPosition = mainSplitPane.dividers.first().position
        mainSplitPane.items.remove(sidePanelContentView.root)
    }

    fun showSidePanelContent() {
        mainSplitPane.items.add(0, sidePanelContentView.root)
        mainSplitPane.setDividerPosition(0, lastSideAndSceneDividerPosition)
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
