package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class CatalogueViewStylesheet : Stylesheet() {

    companion object {
        val mfxCheckListView by cssclass("mfx-check-list-view")
        val mfxCheckListCell by cssclass("mfx-check-list-cell")
        val mfxCheckBox by cssclass("mfx-checkbox")
        val rippleContainer by cssclass("ripple-container")
        val mfxRippleGenerator by cssclass("mfx-ripple-generator")
        val mfxRippleColor by cssproperty<MultiValue<Paint>>("-mfx-ripple-color")

        val backgroundColour: Color = Color.valueOf(Style.BackgroundColor)
        val surfaceColor: Color = Color.valueOf(Style.SurfaceColor)
        val primaryColor: Color = Color.valueOf(Style.PrimaryColor)
        val secondaryColor: Color = Color.valueOf(Style.SecondaryColor)
    }

    val segmentedButton by cssclass()

    init {
        segmentedButton {
            toggleButton {
                backgroundColor += surfaceColor
                borderColor += box(primaryColor)

                prefWidth = 80.px
                prefHeight = 30.px
                and(selected, pressed) {
                    backgroundColor += primaryColor
                    textFill = surfaceColor
                }
            }
        }

        mfxCheckListView {
            virtualFlow {
                scrollBar {
                    prefWidth = 10.0.px
                    track {
                        backgroundColor += Color.TRANSPARENT
                    }
                    thumb {
                        backgroundColor += primaryColor
                    }
                }
            }

            backgroundColor += surfaceColor
            mfxCheckListCell {
                borderColor += box(Color.TRANSPARENT)

                and(hover) {
                    backgroundColor += backgroundColour
                }
                and(selected, checked) {
                    and(hover) {
                        backgroundColor += backgroundColour
                    }
                    backgroundColor += surfaceColor
                }
            }
        }

        mfxCheckBox {
            rippleContainer {
                mfxRippleGenerator {
                    mfxRippleColor.value += secondaryColor
                }
            }
            and(selected) {
                box {
                    backgroundColor += secondaryColor
                    borderColor += box(secondaryColor)
                }
            }
            box {
                borderColor += box(primaryColor)
            }
        }

        listView {
            backgroundColor += surfaceColor
        }

        listCell {
            backgroundColor += surfaceColor
        }
    }
}
