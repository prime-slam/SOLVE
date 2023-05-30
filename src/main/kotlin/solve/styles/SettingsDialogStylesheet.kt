package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class SettingsDialogStylesheet : Stylesheet() {
    init {
        colorPicker {
            backgroundColor += BackgroundColor
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
                    mfxRippleColor.value += CatalogueViewStylesheet.SecondaryColor
                }
            }
            and(selected) {
                box {
                    backgroundColor += CatalogueViewStylesheet.SecondaryColor
                    borderColor += box(CatalogueViewStylesheet.SecondaryColor)
                }
            }
            box {
                borderColor += box(CatalogueViewStylesheet.PrimaryColor)
            }
        }

        mfxSlider {
            bar {
                mfxRippleGenerator {
                    mfxColor.value += PrimaryColor
                    mfxRippleColor.value += PrimaryColor
                }
                fill = Color.valueOf(Style.PrimaryColor)
            }
            track {

                mfxColor.value = backgroundColor
                fill = BackgroundColor
            }

            thumbContainer {
                and(hover) {
                    thumbRadius {
                        mfxColor.value += PrimaryColor
                    }
                }

                and(pressed) {
                    thumbRadius {
                        mfxColor.value += PrimaryColor
                    }
                }

                mfxRippleGenerator {
                    mfxRippleColor.value += PrimaryColor
                }

                thumb {
                    mfxColor.value += PrimaryColor
                    mfxRippleGenerator {
                        mfxRippleColor.value += PrimaryColor
                    }
                }
            }
        }
    }

    companion object {
        private val mfxSlider by cssclass()
        private val thumbContainer by cssclass("thumb-container")
        private val thumbRadius by cssclass("thumb-radius")

        val mfxColor by cssproperty<MultiValue<Paint>>("-mfx-color")
        val mfxRippleGenerator by cssclass("mfx-ripple-generator")
        val mfxRippleColor by cssproperty<MultiValue<Paint>>("-mfx-ripple-color")
        val mfxCheckBox by cssclass("mfx-checkbox")
        val rippleContainer by cssclass("ripple-container")

        val BackgroundColor: Color = Color.valueOf(Style.BackgroundColor)
        val PrimaryColor: Color = Color.valueOf(Style.PrimaryColor)
    }
}
