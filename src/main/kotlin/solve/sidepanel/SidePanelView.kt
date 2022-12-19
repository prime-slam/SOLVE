package solve.sidepanel

import javafx.geometry.Side
import javafx.scene.control.ContentDisplay
import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.paint.Color
import solve.catalogue.view.CatalogueView
import solve.utils.createPxBoxWithValue
import solve.utils.loadImage
import tornadofx.*

class SidePanelView: View() {
    companion object {
        private const val TabIconSize = 20.0
    }

    private val catalogueView: CatalogueView by inject()
    private val filterPanelView = pane() // TODO: add a filter panel.
    private val catalogueTabIconImage = loadImage("icons/sidepanel_catalogue_icon.png")
    private val filterTabIconImage = loadImage("icons/sidepanel_filter_icon.png")

    override val root = tabpane {
        addStylesheet(SidePanelTapPaneStyle::class)
        side = Side.LEFT
        tab("Catalogue") {
            add(catalogueView)
            if (catalogueTabIconImage != null) {
                graphic = createTabIconImageView(catalogueTabIconImage)
            }
        }
        tab("Filter") {
            add(filterPanelView)
            if (filterTabIconImage != null) {
                graphic = createTabIconImageView(filterTabIconImage)
            }
        }
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }

    private fun createTabIconImageView(iconImage: Image) = imageview(iconImage) {
        fitHeight = TabIconSize
        isPreserveRatio = true
    }
}

class SidePanelTapPaneStyle: Stylesheet() {
    companion object {
        private val DefaultTabColor = Color.TRANSPARENT
        private val SelectedTabColor = Color.LIGHTGRAY
        private val TabPaneBackgroundColor = Color.TRANSPARENT
        private const val TabLength = 100.0
    }
    init {
        tab {
            backgroundColor += DefaultTabColor
            backgroundInsets += createPxBoxWithValue(0.0)
            backgroundRadius += createPxBoxWithValue(0.0)
            prefWidth = Dimension(TabLength, Dimension.LinearUnits.px)
            tabLabel {
                contentDisplay = ContentDisplay.LEFT
                prefWidth = Dimension(TabLength, Dimension.LinearUnits.px)
            }
            and(selected) {
                backgroundColor += SelectedTabColor
                focusColor = Color.TRANSPARENT
            }
        }
        tabPane {
            tabHeaderBackground {
                backgroundColor += TabPaneBackgroundColor
            }
        }
    }
}
