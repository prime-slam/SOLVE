package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class CatalogueViewStylesheet : Stylesheet() {
    init {
        segmentedButton {
            toggleButton {
                backgroundColor += SurfaceColor
                borderColor += box(PrimaryColor)

                prefWidth = 80.px
                prefHeight = 30.px
                and(selected, pressed) {
                    backgroundColor += PrimaryColor
                    textFill = SurfaceColor
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
                        backgroundColor += PrimaryColor
                    }
                }
            }

            backgroundColor += SurfaceColor
            mfxCheckListCell {
                borderColor += box(Color.TRANSPARENT)

                and(hover) {
                    backgroundColor += BackgroundColor
                }
                and(selected, checked) {
                    and(hover) {
                        backgroundColor += BackgroundColor
                    }
                    backgroundColor += SurfaceColor
                }
            }
        }

        mfxCheckBox {
            rippleContainer {
                mfxRippleGenerator {
                    mfxRippleColor.value += SecondaryColor
                }
            }
            and(selected, indeterminate) {
                box {
                    backgroundColor += SecondaryColor
                    borderColor += box(SecondaryColor)
                }
            }
            box {
                borderColor += box(PrimaryColor)
            }
        }

        listView {
            backgroundColor += SurfaceColor
        }

        listCell {
            backgroundColor += SurfaceColor
        }
    }

    companion object {
        val mfxCheckListView by cssclass("mfx-check-list-view")
        val mfxCheckListCell by cssclass("mfx-check-list-cell")
        val mfxCheckBox by cssclass("mfx-checkbox")
        val rippleContainer by cssclass("ripple-container")
        val mfxRippleGenerator by cssclass("mfx-ripple-generator")
        val mfxRippleColor by cssproperty<MultiValue<Paint>>("-mfx-ripple-color")
        val segmentedButton by cssclass()

        val BackgroundColor: Color = Color.valueOf(Style.BackgroundColor)
        val SurfaceColor: Color = Color.valueOf(Style.SurfaceColor)
        val PrimaryColor: Color = Color.valueOf(Style.PrimaryColor)
        val SecondaryColor: Color = Color.valueOf(Style.SecondaryColor)
    }
}
