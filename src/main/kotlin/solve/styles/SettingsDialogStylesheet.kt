package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class SettingsDialogStylesheet : Stylesheet() {

    companion object {
        private val mfxSlider by cssclass()
        private val thumbContainer by cssclass("thumb-container")

        val mfxColor by cssproperty<MultiValue<Paint>>("-mfx-color")
        val mfxRippleGenerator by cssclass("mfx-ripple-generator")
        val mfxRippleColor by cssproperty<MultiValue<Paint>>("-mfx-ripple-color")
        val mfxCheckBox by cssclass("mfx-checkbox")
        val rippleContainer by cssclass("ripple-container")

        val backgroundColour: Color = Color.valueOf(Style.BackgroundColor)
        val primaryColor: Color = Color.valueOf(Style.PrimaryColor)
    }

    init {
        colorPicker {
            backgroundColor += backgroundColour
            borderColor += box(Color.TRANSPARENT)
            arrowButton {
                arrow {
                    backgroundColor += Color.valueOf(Style.PrimaryColor)
                }
            }
        }

        mfxCheckBox {
            rippleContainer {
                mfxRippleGenerator {
                    mfxRippleColor.value += CatalogueViewStylesheet.secondaryColor
                }
            }
            and(selected) {
                box {
                    backgroundColor += CatalogueViewStylesheet.secondaryColor
                    borderColor += box(CatalogueViewStylesheet.secondaryColor)
                }
            }
            box {
                borderColor += box(CatalogueViewStylesheet.primaryColor)
            }
        }

        mfxSlider {
            bar {
                mfxRippleGenerator {
                    mfxColor.value += primaryColor
                    mfxRippleColor.value += primaryColor
                }
                fill = Color.valueOf(Style.PrimaryColor)
            }
            track {

                mfxColor.value = backgroundColor
                fill = backgroundColour
            }

            thumbContainer {
                mfxRippleGenerator {
                    mfxRippleColor.value += primaryColor
                }

                thumb {
                    mfxColor.value += primaryColor
                    mfxRippleGenerator {
                        mfxRippleColor.value += primaryColor
                    }
                }
            }
        }
    }
}
