package solve.importer.view

import io.github.palexdev.materialfx.controls.MFXContextMenu
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.cancel
import solve.constants.IconsAlertError
import solve.styles.Style
import solve.utils.loadResourcesImage
import solve.utils.materialfx.action
import solve.utils.materialfx.item
import solve.utils.materialfx.mfxButton
import solve.utils.materialfx.mfxContextMenu
import tornadofx.*

class AlertDialog(contentText: String) : View() {
    private val errorIcon = loadResourcesImage(IconsAlertError)

    override val root =
        borderpane {
            top {
                label("Error") {
                    graphicTextGap = 15.0
                    graphic = ImageView(errorIcon)
                    BorderPane.setMargin(this, Insets(0.0, 0.0, 0.0, 14.0))
                    prefHeight = 0.0
                    style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.HeaderFontSize};"
                }
            }
            center {
                label(contentText) {
                    mfxContextMenu {
                        style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.MainFontSize};"
                        copyError(text)
                    }

                    BorderPane.setMargin(this, Insets(0.0, 14.0, 0.0, 14.0))
                    style =
                        "-fx-font-family: ${Style.Font}; -fx-font-size: ${Style.MainFontSize};  -fx-text-fill: #000000;"
                    BorderPane.setAlignment(this, Pos.CENTER_LEFT)
                    isWrapText = true
                    prefWidth = 300.0
                }
            }

            bottom {
                mfxButton("OK") {
                    BorderPane.setMargin(this, Insets(0.0, 14.0, 14.0, 0.0))
                    maxWidth = 40.0
                    prefHeight = 23.0
                    style =
                        "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.ButtonFontSize}; " +
                        "-fx-font-weight: ${Style.FontWeightBold}; -fx-text-fill: #78909C;"
                    BorderPane.setAlignment(this, Pos.TOP_RIGHT)
                    prefWidth = 180.0
                    action {
                        find<ControlPanel>().coroutineScope.cancel()
                        close()
                    }
                }
            }
        }

    private fun MFXContextMenu.copyError(text: String) {
        item("Copy Error").action {
            val clipboardContent = ClipboardContent().also { it.putString(text) }
            Clipboard.getSystemClipboard().setContent(clipboardContent)
        }
    }
}
