package solve.sidepanel.tabs

import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.paint.Color
import solve.main.splitpane.SidePanelLocation
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentController
import solve.utils.DarkLightGrayColor
import solve.utils.createPxBoxWithValue
import tornadofx.*

class SidePanelTabsView: View() {
    companion object {
        private const val TabIconSize = 20.0
    }

    private val location: SidePanelLocation by param()
    private val tabs: List<SidePanelTab> by param()

    private val contentControllerParams = "location" to location
    private val contentController: SidePanelContentController by inject(scope, contentControllerParams)

    private val initialTab = tabs.first()

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
                        val iconImageView = createTabIconImageView(it)
                        graphic = pane {
                            add(iconImageView)
                            paddingLeft = 5
                        }
                    }
                }
                rotate = -90.0
            }

            setPrefSize(30.0, 100.0)
            usePrefSize = true

            action {
                onTabSelected(tab)
            }
        }
        tabsVBox.add(tabButton)
        tabsToggleButtonsMap[tab] = tabButton
    }

    private fun createTabIconImageView(iconImage: Image) = imageview(iconImage) {
        fitHeight = TabIconSize
        isPreserveRatio = true
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
        tabsToggleGroup.selectToggle(tabsToggleButtonsMap[initialTab])
        onTabSelected(initialTab)
    }
}

class SidePanelTabsStyle: Stylesheet() {
    companion object {
        private val DefaultTabColor = Color.TRANSPARENT
        private val HoveredTabColor = Color.LIGHTGRAY
        private val PressedTabColor = DarkLightGrayColor
    }
    init {
        Stylesheet.toggleButton {
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
}
