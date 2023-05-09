package solve.main

import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.css.themes.MFXThemeManager
import io.github.palexdev.materialfx.css.themes.Themes
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import javafx.geometry.Insets
import javafx.scene.control.ContentDisplay
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import solve.catalogue.view.CatalogueView
import solve.constants.IconsHelp
import solve.constants.IconsImportFab
import solve.constants.IconsPlugins
import solve.constants.IconsSettings
import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import solve.main.splitpane.SidePanelLocation
import solve.main.splitpane.SidePanelSplitPane
import solve.scene.view.SceneView
import solve.settings.grid.view.GridSettingsView
import solve.settings.visualization.VisualizationSettingsView
import solve.sidepanel.SidePanelTab
import solve.sidepanel.content.SidePanelContentView
import solve.sidepanel.tabs.SidePanelTabsView
import solve.styles.MFXButtonStyleSheet
import solve.styles.Style
import solve.utils.MaterialFXDialog
import solve.utils.createPxBox
import solve.utils.loadResourcesImage
import solve.utils.mfxButton
import tornadofx.*

class MainView : View() {
    companion object {
        private const val LeftSidePanelAndSceneDividerPosition = 0.25
        private const val RightSidePanelAndSceneDividerPosition = 0.88

        private const val TabsViewLocationParamName = "location"
        private const val TabsViewTabsParamName = "tabs"
        private const val TabsViewInitialTabParamName = "initialTab"

        const val ProjectTabName = "Project"
        const val LayersTabName = "Layers"
        const val GridTabName = "Grid"

        private val importIcon = loadResourcesImage(IconsImportFab)
        private val pluginsIcon = loadResourcesImage(IconsPlugins)
        private val settingsIcon = loadResourcesImage(IconsSettings)
        private val helpIcon = loadResourcesImage(IconsHelp)
    }

    val importer: ImporterView by inject()

    private val mainView: MainView by inject()

    val controller: ImporterController by inject()

    private val sceneView: SceneView by inject()

    var content = MFXGenericDialog()
    var dialog = MFXStageDialog()

    private lateinit var mainViewSplitPane: SidePanelSplitPane

    private val leftSidePanelTabs = listOf(
        SidePanelTab(
            ProjectTabName,
            find<CatalogueView>().root
        )
    )

    private val rightSidePanelTabs = listOf(
        SidePanelTab(
            LayersTabName,
            find<VisualizationSettingsView>().root
        ),
        SidePanelTab(
            GridTabName,
            find<GridSettingsView>().root
        )
    )

    private val leftSidePanelViews =
        createSidePanelsViews(leftSidePanelTabs, SidePanelLocation.Left, leftSidePanelTabs.first())
    private val rightSidePanelViews =
        createSidePanelsViews(rightSidePanelTabs, SidePanelLocation.Right)

    private val importFab = mfxButton {
        VBox.setMargin(this, Insets(5.0, 8.0, 10.0, 8.0))

        val circle = Circle(this.layoutX + Style.FabRadius, this.layoutY + Style.FabRadius, Style.FabRadius)
        clip = circle
        graphic = ImageView(importIcon)
        setPrefSize(56.0, 56.0)
        style = "-fx-background-color: #${Style.secondaryColor}; -fx-background-radius: 28;"
        action {
            importAction()
        }
    }

    private val pluginsButton = createTabButton("Plugins", pluginsIcon)

    private val settingsButton = createTabButton("Settings", settingsIcon)

    private val helpButton = createTabButton("Help", helpIcon)

    private val nameApp = label("SOLVE") {
        style = "-fx-font-family: ${Style.font}; -fx-font-weight:700; -fx-font-size: 18px"
        VBox.setMargin(this, Insets(0.0, 6.0, 0.0, 6.0))
    }

    private val leftPanel = vbox(7) {
        addStylesheet(MFXButtonStyleSheet::class)
        style = "-fx-background-color: #${Style.surfaceColor}"
        add(nameApp)
        add(importFab)
        add(leftSidePanelViews.tabsView.root)
    }

    private val rightPanel = vbox(7) {
        addStylesheet(MFXButtonStyleSheet::class)
        style = "-fx-background-color: #${Style.surfaceColor}"
        add(rightSidePanelViews.tabsView.root)
    }

    private val mainViewBorderPane = borderpane {
        right = rightPanel
        left = leftPanel

        val splitPaneDividersPositions = listOf(
            LeftSidePanelAndSceneDividerPosition,
            RightSidePanelAndSceneDividerPosition
        )
        val splitPaneContainedNodes = listOf(
            leftSidePanelViews.contentView.root,
            sceneView.root,
            rightSidePanelViews.contentView.root
        )

        mainViewSplitPane = SidePanelSplitPane(
            splitPaneDividersPositions,
            splitPaneContainedNodes,
            SidePanelLocation.Both,
            SidePanelLocation.Left
        )
        mainViewSplitPane.addStylesheet(MainSplitPaneStyle::class)
        center = mainViewSplitPane
    }

    override val root = mainViewBorderPane

    init {
        accelerators[KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)] = {
            importAction()
        }
        accelerators[KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)] = {
            leftSidePanelViews.tabsView.selectTab(ProjectTabName)
        }
        accelerators[KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN)] = {
            rightSidePanelViews.tabsView.selectTab(LayersTabName)
        }
        accelerators[KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN)] = {
            rightSidePanelViews.tabsView.selectTab(GridTabName)
        }
    }

    private fun createTabButton(text: String, icon: Image?): MFXButton {
        return mfxButton(text) {
            clip = Style.circleForRipple(this)
            styleClass.add("mfxButton")
            setPrefSize(Style.navigationRailTabSize, Style.navigationRailTabSize)
            paddingAll = 0.0
            contentDisplay = ContentDisplay.TOP
            graphic = ImageView(icon)
            style = Style.tabStyle
        }
    }

    private fun importAction() {
        controller.directoryPath.set(null)
        controller.projectAfterPartialParsing.set(null)
        content = MaterialFXDialog.createGenericDialog(importer.root)
        dialog = MaterialFXDialog.createStageDialog(content, mainView.currentStage, mainView.root)
        dialog.show()
        content.padding = Insets(0.0, 0.0, 10.0, 0.0)
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        MFXThemeManager.addOn(root.scene, Themes.DEFAULT)
    }

    fun hideSidePanelContent(location: SidePanelLocation) {
        mainViewSplitPane.hideNodeAt(location)
    }

    fun showSidePanelContent(location: SidePanelLocation) {
        mainViewSplitPane.showNodeAt(location)
    }

    private fun createSidePanelsViews(
        tabs: List<SidePanelTab>,
        location: SidePanelLocation,
        initialTab: SidePanelTab? = null
    ): SidePanelViews {
        val scope = Scope()

        val contentView = find<SidePanelContentView>(scope)

        val tabsViewParams = mapOf(
            TabsViewLocationParamName to location,
            TabsViewTabsParamName to tabs,
            TabsViewInitialTabParamName to initialTab
        )
        val tabsView = find<SidePanelTabsView>(scope, tabsViewParams)

        return SidePanelViews(tabsView, contentView)
    }

    private data class SidePanelViews(val tabsView: SidePanelTabsView, val contentView: SidePanelContentView)
}

class MainSplitPaneStyle : Stylesheet() {
    init {
        splitPane {
            splitPaneDivider {
                padding = createPxBox(0.0, 1.0, 0.0, 1.0)
            }
        }
    }
}
