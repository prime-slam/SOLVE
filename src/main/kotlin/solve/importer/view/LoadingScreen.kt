package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXProgressSpinner
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlinx.coroutines.cancel
import solve.styles.Style
import solve.utils.mfxButton
import tornadofx.*

class LoadingScreen : View("Loading") {
    private val primaryColor: Color = Color.valueOf(Style.primaryColor)

    private val progressSpinner = MFXProgressSpinner().apply {
        color1 = primaryColor
        color2 = primaryColor
        color3 = primaryColor
        color4 = primaryColor
    }

    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override val root =
        borderpane {
            prefHeight = 570.0
            prefWidth = 453.0
            style = "-fx-background-color: #ffffff"
            top{
                label("Import a directory") {
                    prefHeight=0.0
                    prefWidth=141.0
                    font = Font.font(Style.fontCondensed, 20.0)
                    BorderPane.setMargin(this, Insets(0.0, 0.0, 0.0, 24.0))
                }
            }
            center {

                vbox(15) {
                    BorderPane.setMargin(this, Insets(200.0, 0.0,0.0,170.0))
                    add(progressSpinner.apply {
                        VBox.setMargin(this, Insets(0.0,0.0,0.0,30.0))
                    })
                    label("Please, wait...") {
                        style = "-fx-font-size: 20px"
                    }
                }
            }
            bottom {
                mfxButton("CANCEL") {
                    BorderPane.setMargin(this, Insets(0.0, 24.0, 24.0, 0.0))
                    maxWidth = 75.0
                    prefHeight = 23.0
                    style = Style.buttonStyle
                    BorderPane.setAlignment(this, Pos.TOP_RIGHT)
                    prefWidth = 180.0
                    action {
                        find<ControlPanel>().coroutineScope.cancel()
                        close()
                    }
                }
            }
        }
}
