package solve.main

import com.huskerdev.openglfx.GLCanvasAnimator
import com.huskerdev.openglfx.OpenGLCanvas
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor
import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.css.themes.MFXThemeManager
import io.github.palexdev.materialfx.css.themes.Themes
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import org.eclipse.fx.drift.DriftFXSurface
import org.eclipse.fx.drift.GLRenderer
import org.eclipse.fx.drift.PresentationMode
import org.eclipse.fx.drift.Renderer
import org.eclipse.fx.drift.StandardTransferTypes
import org.eclipse.fx.drift.Swapchain
import org.eclipse.fx.drift.SwapchainConfig
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
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
import solve.styles.TooltipStyle
import solve.utils.MaterialFXDialog
import solve.utils.createPxBox
import solve.utils.loadResourcesImage
import solve.utils.mfxButton
import tornadofx.*
import java.nio.ByteBuffer

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

    private var swapchain: Swapchain? = null
    private lateinit var renderer: Renderer
    private var glContextID = 0L
    private var framebufferID = 0
    private var depthTextureID = 0

    var content = MFXGenericDialog()
    var dialog = MFXStageDialog()

    private lateinit var mainViewSplitPane: SidePanelSplitPane

    private val leftSidePanelTabs = listOf(
        SidePanelTab(
            ProjectTabName,
            find<CatalogueView>().root,
            "Ctrl+P"
        )
    )

    private val rightSidePanelTabs = listOf(
        SidePanelTab(
            LayersTabName,
            find<VisualizationSettingsView>().root,
            "Ctrl+L"
        ),
        SidePanelTab(
            GridTabName,
            find<GridSettingsView>().root,
            "Ctrl+G"
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
        isFocusTraversable = false
        tooltip("Ctrl+I")
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
        top = createDriftFXCanvas()
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

    private fun createOpenGLFXCanvas(): Region {
        val canvas = OpenGLCanvas.create(LWJGLExecutor.LWJGL_MODULE)
        canvas.animator = GLCanvasAnimator(200.0)
        canvas.minWidth = 100.0
        canvas.minHeight = 1000.0

        canvas.addOnReshapeEvent(ExampleRenderer::reshape)
        canvas.addOnRenderEvent(ExampleRenderer::render)
        canvas.addOnInitEvent(ExampleRenderer::canvasInit)

        return canvas
    }

    private fun createDriftFXCanvas(): Node {
        val surface = DriftFXSurface()
        surface.minHeight = 1000.0
        renderer = GLRenderer.getRenderer(surface)

        Platform.runLater {
            val thread = Thread(this::driftFXLoop)
            thread.isDaemon = true
            thread.start()
        }

        return surface
    }

    private fun driftFXLoop() {
        try {
            glContextID = org.eclipse.fx.drift.internal.GL.createSharedCompatContext(0)
            org.eclipse.fx.drift.internal.GL.makeContextCurrent(glContextID)

            GL.createCapabilities()

            while (true) {

                val size = renderer.size

                if (swapchain == null || size.x != swapchain?.config?.size?.x || size.y != swapchain?.config?.size?.y) {
                    // re-create the swapchain
                    if (swapchain != null) {
                        swapchain?.dispose()
                    }
                    swapchain = renderer.createSwapchain(
                            SwapchainConfig(
                                size,
                                2,
                                PresentationMode.MAILBOX,
                                StandardTransferTypes.MainMemory
                            )
                        )
                }

                val currentSwapchain = swapchain

                if (currentSwapchain != null) {
                    val target = currentSwapchain.acquire()

                    val texId = GLRenderer.getGLTextureId(target)
                    texId.toString()

                    depthTextureID = glGenTextures()

                    // update depth tex
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureID)
                    GL11.glTexImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        GL30.GL_DEPTH_COMPONENT32F,
                        size.x,
                        size.y,
                        0,
                        GL11.GL_DEPTH_COMPONENT,
                        GL11.GL_FLOAT,
                        null as ByteBuffer?
                    )

                    framebufferID = GL30.glGenFramebuffers()
                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID)
                    GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texId, 0)
                    GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTextureID, 0)

                    val status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
                    when (status) {
                        GL30.GL_FRAMEBUFFER_COMPLETE -> {}
                        GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> System.err.println("INCOMPLETE_ATTACHMENT!")
                    }

                    GL11.glViewport(0, 0, size.x, size.y)

                    GL30.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)

                    GL30.glBegin(GL30.GL_QUADS)
                    GL30.glColor3f(1.0f, 0.5f, 0.0f)
                    GL30.glVertex2d(-10.0, 0.0)
                    GL30.glVertex2d(10.0, 0.0)
                    GL30.glVertex2d(10.0, 10.0 / 2)
                    GL30.glVertex2d(-10.0, 10.0 / 2)
                    GL30.glEnd()

                    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
                    GL30.glDeleteFramebuffers(framebufferID)
                    GL11.glDeleteTextures(depthTextureID)

                    currentSwapchain.present(target)
                }
                Thread.sleep(100L)
            }
        }
        catch (error: Exception) {
            println(123)
        }
    }

    override val root = mainViewBorderPane

    init {
        root.addStylesheet(TooltipStyle::class)
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
