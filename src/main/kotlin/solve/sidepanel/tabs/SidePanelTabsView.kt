package solve.sidepanel.tabs

import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.paint.Color
import solve.main.splitpane.SidePanelLocation
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentController
import solve.utils.DarkLightGrayColor
import solve.utils.createImageViewIcon
import solve.utils.createPxBoxWithValue
import tornadofx.*

class SidePanelTabsView: View() {
    private val location: SidePanelLocation by param()
    private val tabs: List<SidePanelTab> by param()
    private val initialTab: SidePanelTab? by param()

    private val haveInitialTab: Boolean
        get() = initialTab != null

    private val contentControllerParams = mapOf("location" to location, "isContentShowingParam" to haveInitialTab)
    private val contentController: SidePanelContentController by inject(scope, contentControllerParams)

    private val tabsVBox = vbox()
    private val tabsToggleGroup = ToggleGroup()
    private val tabsToggleButtonsMap = mutableMapOf<SidePanelTab, ToggleButton>()

    override val root = tabsVBox.also {
        it.addStylesheet(SidePanelTabsStyle::class)
    }

    init {
        addTabs()
        initializeTabsToggle()
    }

    private fun addTab(tab: SidePanelTab) {
        val tabButton = togglebutton(tabsToggleGroup) {
            graphic = group {
                label(tab.name) {
                    tab.icon?.let {
                        val iconImageView = createImageViewIcon(it, TabIconSize)
                        graphic = pane {
                            add(iconImageView)
                            paddingLeft = 5
                        }
                    }
                }
                rotate = -90.0
            }

            setPrefSize(TabWidth, TabOffsetSpaceSize + TabIconSize + TabLabelSymbolSize * tab.name.count())
            usePrefSize = true

            action {
                onTabSelected(tab)
            }
        }
        tabsVBox.add(tabButton)
        tabsToggleButtonsMap[tab] = tabButton
    }

    private fun onTabSelected(tab: SidePanelTab) {
        if (tabsToggleGroup.selectedToggle != null) {
            contentController.showContent(tab.contentNode)
        } else {
            contentController.clearContent()
        }
    }

    private fun addTabs() {
        tabs.forEach { addTab(it) }
    }

    private fun initializeTabsToggle() {
        if (haveInitialTab) {
            if (tabs.contains(initialTab)) {
                tabsToggleGroup.selectToggle(tabsToggleButtonsMap[initialTab])
                onTabSelected(initialTab ?: return)
            }
        } else {
            tabsToggleGroup.selectToggle(null)
        }
    }

    companion object {
        private const val TabIconSize = 20.0
        private const val TabWidth = 30.0
        private const val TabLabelSymbolSize = 4.4
        private const val TabOffsetSpaceSize = 35.0
    }
}

class SidePanelTabsStyle: Stylesheet() {
    init {
        toggleButton {
            backgroundColor += DefaultTabColor
            backgroundInsets += createPxBoxWithValue(0.0)
            backgroundRadius += createPxBoxWithValue(0.0)

            and(hover) {
                backgroundColor += HoveredTabColor
                focusColor = Color.TRANSPARENT
            }

            and(pressed) {
                backgroundColor += PressedTabColor
            }

            and(selected) {
                backgroundColor += PressedTabColor
            }
        }
    }

    companion object {
        private val DefaultTabColor = Color.TRANSPARENT
        private val HoveredTabColor = Color.LIGHTGRAY
        private val PressedTabColor = DarkLightGrayColor
    }
}
