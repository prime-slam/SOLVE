package solve.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import solve.constants.IconsProjectFilled
import solve.constants.IconsLayers
import solve.constants.IconsLayersFilled
import solve.constants.IconsProject
import solve.utils.createPxBoxWithValue
import tornadofx.Stylesheet
import tornadofx.cssclass
import java.net.URI

class SidePanelTabsStyle : Stylesheet() {
    val project by cssclass()
    val layers by cssclass()
    val grid by cssclass()

    init {
        toggleButton {
            backgroundColor += Paint.valueOf(Style.surfaceColor)
            backgroundInsets += createPxBoxWithValue(0.0)
            backgroundRadius += createPxBoxWithValue(0.0)
            textFill = Paint.valueOf(Style.primaryColorLight)
            and(selected) {
                textFill = Paint.valueOf(Style.primaryColor)
            }
        }
        project {
            graphic = URI(IconsProject)

            and(hover) {
                backgroundColor += Paint.valueOf(Style.backgroundColour)
                focusColor = Color.TRANSPARENT
            }
            and(selected) {
                backgroundColor += Paint.valueOf(Style.surfaceColor)
                graphic = URI(IconsProjectFilled)
            }
        }

        layers {
            graphic = URI(IconsLayers)

            and(hover) {
                backgroundColor += Paint.valueOf(Style.backgroundColour)
                focusColor = Color.TRANSPARENT
            }
            and(selected) {
                backgroundColor += Paint.valueOf(Style.surfaceColor)
                graphic = URI(IconsLayersFilled)
            }
        }

        grid {
            graphic = URI(IconsLayers)

            and(hover) {
                backgroundColor += Paint.valueOf(Style.backgroundColour)
                focusColor = Color.TRANSPARENT
            }
            and(selected) {
                backgroundColor += Paint.valueOf(Style.surfaceColor)
                graphic = URI(IconsLayersFilled)
            }
        }
    }
}