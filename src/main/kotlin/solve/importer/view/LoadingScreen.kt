package solve.importer.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlinx.coroutines.cancel
import solve.constants.IconsImporterLoadingPath
import solve.utils.loadResourcesImage
import tornadofx.*

class LoadingScreen : View("Loading") {
    private val loading = loadResourcesImage(IconsImporterLoadingPath)

    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override val root =
        borderpane {
            padding = Insets(0.0, 0.0, 8.0, 0.0)
            center {
                label {
                    text = "Please, wait..."
                    font = Font.font(20.0)
                    graphic = ImageView(loading).apply {
                        fitHeight = 80.0
                        fitWidth = 80.0
                    }
                    contentDisplay = ContentDisplay.TOP
                }
            }
            bottom {
                button("Cancel") {
                    BorderPane.setAlignment(this, Pos.BOTTOM_CENTER)
                    maxWidth = 130.0
                    action {
                        find<ControlPanel>().coroutineScope.cancel()
                        close()
                    }
                }
            }
        }
}
