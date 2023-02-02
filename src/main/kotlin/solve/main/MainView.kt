package solve.main

import javafx.application.Platform
import javafx.scene.control.SplitPane
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

    private lateinit var mainSplitPane: SplitPane
    private var lastSideAndSceneDividerRatio = SideAndSceneDividerRatio

    private var isWindowResizing = false
    private var isWindowResizingDetectionInitialized = false

    private val mainViewBorderPane = borderpane {
        top<MenuBarView>()
        mainSplitPane = splitpane {
            addStylesheet(MainSplitPaneStyle::class)
            add(sidePanelContentView)
            add(sceneView)
            SplitPane.setResizableWithParent(sidePanelContentView.root, false)
            SplitPane.setResizableWithParent(sceneView.root, false)
            setDividerPosition(0, SideAndSceneDividerRatio)

            dividers.first().positionProperty().onChange {
                onDividerPositionChanged()
            }
        }
        center = mainSplitPane
        left<SidePanelTabsView>()
    }

    override val root = mainViewBorderPane

    fun hideSidePanelContent() {
        mainSplitPane.items.remove(sidePanelContentView.root)
    }

    fun showSidePanelContent() {
        mainSplitPane.items.add(0, sidePanelContentView.root)
        mainSplitPane.setDividerPosition(0, lastSideAndSceneDividerRatio)
    }

    private fun initializeWindowResizingDetection() {
        mainViewBorderPane.scene.addPreLayoutPulseListener {
            isWindowResizing = true
            Platform.runLater {
                isWindowResizing = false
            }
        }
    }

    private fun onDividerPositionChanged() {
        if (!isWindowResizingDetectionInitialized) {
            initializeWindowResizingDetection()
            isWindowResizingDetectionInitialized = true
            return
        }

        if (!isWindowResizing) {
            lastSideAndSceneDividerRatio = mainSplitPane.dividers.first().position
        }
        mainSplitPane.setDividerPosition(0, lastSideAndSceneDividerRatio)
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
