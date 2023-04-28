package solve.styles

import javafx.scene.paint.Paint
import solve.settings.visualization.popover.buildSizeSlider
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

class RangeSliderStylesheet : Stylesheet() {

    val rangeSlider by cssclass()
    val lowThumb by cssclass()
    val highThumb by cssclass()
    val rangeBar by cssclass()


    init {
        rangeSlider {
            textFill = Paint.valueOf(Style.surfaceColor)
            track {
                minWidth = 200.px
                backgroundColor += Paint.valueOf(Style.sliderColor)
            }

            s(lowThumb, highThumb, rangeBar) {
                backgroundColor += Paint.valueOf(Style.primaryColor)
            }

        }


    }
}