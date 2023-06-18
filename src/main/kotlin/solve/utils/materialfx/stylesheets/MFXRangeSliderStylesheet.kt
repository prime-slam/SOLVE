package solve.utils.materialfx.stylesheets

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import solve.styles.Style
import solve.utils.createCssBoxWithValue
import solve.utils.scale
import tornadofx.*

class MFXRangeSliderStylesheet : Stylesheet() {
    init {
        rangeSlider {
            lowThumb {
                initThumbStyle()
            }
            highThumb {
                initThumbStyle()
            }

            rangeBar {
                backgroundColor += Paint.valueOf(Style.PrimaryColor)
            }
            track {
                backgroundColor += Paint.valueOf(Style.SettingLightColor)
            }
        }
    }

    private fun CssSelectionBlock.initThumbStyle() {
        borderColor += createCssBoxWithValue(Color.TRANSPARENT)
        backgroundColor += Paint.valueOf(Style.PrimaryColor)
        scale(DefaultThumbScale)
    }

    companion object {
        private val rangeSlider by cssclass()
        private val lowThumb by cssclass()
        private val highThumb by cssclass()
        private val rangeBar by cssclass()

        private const val DefaultThumbScale = 1.3
    }
}
