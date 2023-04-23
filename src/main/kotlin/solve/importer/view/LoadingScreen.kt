package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXProgressSpinner
import javafx.geometry.Insets
<<<<<<< HEAD
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
<<<<<<< HEAD
=======
=======
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
>>>>>>> 5a01415 (added materialfx dialogs and fixed some bugs)
import javafx.stage.Stage
>>>>>>> 4cb42a8 (added materialfx dialogs and fixed some bugs)
import kotlinx.coroutines.cancel
import solve.styles.Style
import solve.utils.mfxButton
import tornadofx.*

class LoadingScreen : View("Loading") {
    private val primaryColor: Color = Color.valueOf(Style.primaryColor)

<<<<<<< HEAD
    private val controlPanel: ControlPanel by inject()

=======
>>>>>>> 5a01415 (added materialfx dialogs and fixed some bugs)
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
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 8eb448d (added borderpane and spaces)
            top {
                label("Import a directory") {
                    hgrow = Priority.ALWAYS
                    prefHeight = 0.0
                    prefWidth = 200.0
                    style = "-fx-font-family: ${Style.fontCondensed}; -fx-font-size: ${Style.headerFontSize}"
=======
            top{
                label("Import a directory") {
                    prefHeight=0.0
                    prefWidth=141.0
                    font = Font.font(Style.fontCondensed, 20.0)
>>>>>>> 5a01415 (added materialfx dialogs and fixed some bugs)
                    BorderPane.setMargin(this, Insets(0.0, 0.0, 0.0, 24.0))
                }
            }
            center {
                vbox(15) {
<<<<<<< HEAD
<<<<<<< HEAD
                    BorderPane.setMargin(this, Insets(200.0, 0.0, 0.0, 170.0))
                    add(
                        progressSpinner.apply {
                            VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 30.0))
                        }
                    )
                    label("Please, wait...") {
                        style = "-fx-font-size: ${Style.headerFontSize}; -fx-font-family: ${Style.fontCondensed}"
=======
                    BorderPane.setMargin(this, Insets(200.0, 0.0,0.0,170.0))
=======
                    BorderPane.setMargin(this, Insets(200.0, 0.0, 0.0, 170.0))
>>>>>>> 8eb448d (added borderpane and spaces)
                    add(progressSpinner.apply {
                        VBox.setMargin(this, Insets(0.0, 0.0, 0.0, 30.0))
                    })
                    label("Please, wait...") {
                        style = "-fx-font-size: 20px"
>>>>>>> 5a01415 (added materialfx dialogs and fixed some bugs)
                    }
                }
            }
            bottom {
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 8eb448d (added borderpane and spaces)
                borderpane {
                    right {
                        mfxButton("CANCEL") {
                            BorderPane.setMargin(this, Insets(0.0, 24.0, 24.0, 0.0))
                            maxWidth = 75.0
                            prefHeight = 23.0
                            style = Style.buttonStyle
                            action {
<<<<<<< HEAD
                                controlPanel.coroutineScope.cancel()
                                close()
                            }
                        }
=======
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
>>>>>>> 5a01415 (added materialfx dialogs and fixed some bugs)
=======
                                find<ControlPanel>().coroutineScope.cancel()
                                close()
                            }
                        }
>>>>>>> 8eb448d (added borderpane and spaces)
                    }
                }
            }
        }
}
