package solve.sidepanel

import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import solve.styles.DarkTheme
import solve.catalogue.view.CatalogueView
import solve.sidepanel.content.SidePanelContentController
import solve.utils.*
import tornadofx.*

class SidePanelTabsView: View() {
    companion object {
        private const val TabIconSize = 20.0
    }

    private val contentController: SidePanelContentController by inject()
    private val catalogueView: CatalogueView by inject()

    private lateinit var catalogueTab: SidePanelTab
    private lateinit var filterTab: SidePanelTab
    private val initialTab: SidePanelTab by lazy { catalogueTab }

    private val catalogueTabIconImage = loadImage("icons/sidepanel_catalogue_icon.png")
    private val filterTabIconImage = loadImage("icons/sidepanel_filter_icon.png")

    private val tabsVBox = vbox()
    private val tabsToggleGroup = ToggleGroup()
    private val tabsToggleButtonsMap = mutableMapOf<SidePanelTab, ToggleButton>()

    override val root = tabsVBox.also {
        it.addClass(DarkTheme.backgroundElement)
        initializeTabs()
        initializeTabsToggleGroup()
    }

    private fun initializeTabs() {
        catalogueTab = SidePanelTab("Catalogue", catalogueTabIconImage, catalogueView.root)
        filterTab = SidePanelTab("Filter", filterTabIconImage, pane()) // TODO: add a filter panel.

        addTab(catalogueTab)
        addTab(filterTab)
    }

    private fun initializeTabsToggleGroup() {
        onTabSelected(initialTab)
        tabsToggleGroup.selectToggle(tabsToggleButtonsMap[initialTab])
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
}

//class SidePanelTabsStyle: Stylesheet() {
//    companion object {
//        private val DefaultTabColor = Color.TRANSPARENT
//        private val HoveredTabColor = Color.LIGHTGRAY
//        private val PressedTabColor = DarkLightGrayColor
//    }
//    init {
//        toggleButton {
//            backgroundColor += DefaultTabColor
//            backgroundInsets += createPxBoxWithValue(0.0)
//            backgroundRadius += createPxBoxWithValue(0.0)
//
//            and(hover) {
//                backgroundColor += DarkTheme.mainColor
//                focusColor = Color.TRANSPARENT
//            }
//
//            and(pressed) {
//                backgroundColor += DarkTheme.mainColor
//            }
//
//            and(selected) {
//                backgroundColor += DarkTheme.mainColor
//            }
//        }
//    }
//}
