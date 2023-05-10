package solve.sidepanel.tabs

import javafx.scene.control.ContentDisplay
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import solve.main.splitpane.SidePanelLocation
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentController
import solve.styles.SidePanelTabsStyle
import solve.styles.Style
import tornadofx.*

open class SidePanelTabsView : View() {
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
        it.style = "-fx-background-color: #${Style.SurfaceColor}"
    }

    init {
        addTabs()
        initializeTabsToggle()
    }

    fun selectTab(name: String) {
        val tab = tabs.single { it.name == name }
        val button = tabsToggleButtonsMap[tab] ?: throw RuntimeException("No toggle button for tab")
        button.fire()
    }

    private fun addTab(tab: SidePanelTab) {
        val tabButton = togglebutton(tab.name, tabsToggleGroup) {
            setPrefSize(Style.NavigationRailTabSize, Style.NavigationRailTabSize)
            clip = Style.circleForRipple(this)
            styleClass.add(tab.name.lowercase())
            contentDisplay = ContentDisplay.TOP
            style =
                "-fx-font-family: ${Style.Font}; -fx-font-weight:700;" +
                " -fx-font-size: ${Style.ButtonFontSize}; -fx-background-radius: 36"

            isFocusTraversable = false
            tooltip(tab.tooltip)
            setPrefSize(72.0, 72.0)
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
}
