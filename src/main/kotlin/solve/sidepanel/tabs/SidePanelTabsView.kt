package solve.sidepanel.tabs

import javafx.scene.control.ContentDisplay
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import solve.main.splitpane.SidePanelLocation
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentController
import solve.styles.Style
import solve.utils.createImageViewIcon
import solve.utils.createPxBoxWithValue
import tornadofx.*
import java.net.URI

class SidePanelTabsView : View() {
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
        it.style = "-fx-background-color: #${Style.surfaceColor}"
    }

    init {
        addTabs()
        initializeTabsToggle()
    }

    private fun addTab(tab: SidePanelTab) {
        val tabButton = togglebutton(tab.name, tabsToggleGroup) {
            contentDisplay = ContentDisplay.TOP
            graphic = tab.icon?.let { createImageViewIcon(it, TabIconSize) }

            style =
                "-fx-font-family: ${Style.font}; -fx-font-weight:700; -fx-border-color: ${Style.secondaryColor}; -fx-font-size: ${Style.buttonFontSize}; -fx-text-fill: ${Style.primaryColorLight}; -fx-background-radius: 36"

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

    companion object {
        private const val TabIconSize = 20.0
        private const val TabWidth = 30.0
        private const val TabLabelSymbolSize = 4.4
        private const val TabOffsetSpaceSize = 35.0
    }
}

class SidePanelTabsStyle : Stylesheet() {
    init {
        toggleButton {
            backgroundColor += Paint.valueOf(Style.surfaceColor)
            backgroundInsets += createPxBoxWithValue(0.0)
            backgroundRadius += createPxBoxWithValue(0.0)
            graphic = URI("/icons/sidepanel/Project.png")

            and(hover) {
                backgroundColor += Paint.valueOf(Style.backgroundColour)
                focusColor = Color.TRANSPARENT
            }
            and(selected) {
                backgroundColor += Paint.valueOf(Style.surfaceColor)
                graphic = URI("/icons/sidepanel/Project2.png")
            }
        }
    }
}
