package solve.sidepanel

import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.paint.Color
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

    private val catalogueTabIconImage = loadImage("icons/sidepanel_catalogue_icon.png")
    private val filterTabIconImage = loadImage("icons/sidepanel_filter_icon.png")

    private val tabsVBox = vbox()
    private val tabsToggleGroup = ToggleGroup()

    override val root = tabsVBox.also {
        initializeTabs()
        it.addStylesheet(SidePanelTabsStyle::class)
    }

    private fun initializeTabs() {
        val catalogueTab = SidePanelTab("Catalogue", catalogueTabIconImage, catalogueView.root)
        val filterTab = SidePanelTab("Filter", filterTabIconImage, pane()) // TODO: add a filter panel.

        addTab(catalogueTab)
        addTab(filterTab)
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
                if (tabsToggleGroup.selectedToggle != null) {
                    contentController.showContent(tab.contentNode)
                } else {
                    contentController.clearContent()
                }
            }
        }
        tabsVBox.add(tabButton)
    }

    private fun createTabIconImageView(iconImage: Image) = imageview(iconImage) {
        fitHeight = TabIconSize
        isPreserveRatio = true
    }
}

class SidePanelTabsStyle: Stylesheet() {
    companion object {
        private val DefaultTabColor = Color.TRANSPARENT
        private val HoveredTabColor = Color.LIGHTGRAY
        private val PressedTabColor = DarkLightGrayColor
    }
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
}
