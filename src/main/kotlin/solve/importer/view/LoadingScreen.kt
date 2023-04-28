package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXProgressSpinner
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.coroutines.cancel
import solve.styles.Style
import solve.utils.mfxButton
import tornadofx.*

class LoadingScreen : View("Loading") {
    private val primaryColor: Color = Color.valueOf(Style.primaryColor)

    private val controlPanel: ControlPanel by inject()

    private val progressSpinner = MFXProgressSpinner().apply {
        color1 = primaryColor
        color2 = primaryColor
        color3 = primaryColor
        color4 = primaryColor
    }

    override val root =
        borderpane {
            prefHeight = 570.0
            prefWidth = 453.0
            style = "-fx-background-color: #ffffff"
            top {
                label("Import a directory") {
                    hgrow = Priority.ALWAYS
                    prefHeight = 0.0
                    prefWidth = 200.0
                    style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.headerFontSize}"
                    BorderPane.setMargin(this, Insets(0.0, 0.0, 0.0, 24.0))
                }
            }
            center {

                vbox(15) {
                    BorderPane.setMargin(this, Insets(200.0, 0.0, 0.0, 170.0))
                    add(progressSpinner.apply {
                        VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 30.0))
                    })
                    label("Please, wait...") {
                        style = "-fx-font-size: ${Style.headerFontSize}; -fx-font-family: ${Style.fontCondensed}"
                    }
                }
            }
            bottom {
                borderpane {
                    right {
                        mfxButton("CANCEL") {
                            BorderPane.setMargin(this, Insets(0.0, 24.0, 24.0, 0.0))
                            maxWidth = 75.0
                            prefHeight = 23.0
                            style = Style.buttonStyle
                            action {
                                controlPanel.coroutineScope.cancel()
                                close()
                            }
                        }
                    }
                }
            }
        }
}
