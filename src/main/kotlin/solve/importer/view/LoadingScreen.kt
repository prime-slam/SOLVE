package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXProgressSpinner
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.coroutines.cancel
import solve.styles.Style
import solve.styles.Style.headerPadding
import solve.utils.materialfx.dialogHeaderLabel
import solve.utils.materialfx.mfxButton
import tornadofx.*

class LoadingScreen : View("Loading") {
    private val PrimaryColor: Color = Color.valueOf(Style.PrimaryColor)

    private val controlPanel: ControlPanel by inject()

    private val progressSpinner = MFXProgressSpinner().apply {
        color1 = PrimaryColor
        color2 = PrimaryColor
        color3 = PrimaryColor
        color4 = PrimaryColor
    }

    override val root =
        borderpane {
            prefHeight = 570.0
            prefWidth = 453.0
            style = "-fx-background-color: #ffffff"
            top {
                dialogHeaderLabel("Import a directory") {
                    padding = headerPadding
                }
            }
            center {

                vbox(15) {
                    BorderPane.setMargin(this, Insets(200.0, 0.0, 0.0, 170.0))
                    add(
                        progressSpinner.apply {
                            VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 30.0))
                        }
                    )
                    label("Please, wait...") {
                        style = "-fx-font-size: ${Style.HeaderFontSize}; -fx-font-family: ${Style.FontCondensed}"
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
                            style = Style.ButtonStyle
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
