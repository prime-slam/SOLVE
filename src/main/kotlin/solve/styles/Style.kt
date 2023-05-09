package solve.styles

import io.github.palexdev.materialfx.controls.MFXButton
import javafx.scene.control.ToggleButton
import javafx.scene.shape.Circle
import tornadofx.*

object Style {
    const val backgroundColor = "EFF0F0"

    const val surfaceColor = "FFFFFF"

    const val tooltipColor = "707070"

    const val primaryColor = "78909C"

    const val onBackgroundColor = "000000"

    const val primaryColorLight = "B0BEC5"

    const val secondaryColor = "41497F"

    const val separatorLineColor = "9DAEB7"

    const val settingLightColor = "EDEEF1"

    const val errorColor = "EF6E6B"

    const val activeColor = "41497F"

    const val fontCondensed = "'Roboto Condensed'"

    const val font = "Roboto"

    const val listFontColor = "3E4345"

    const val headerFontColor = "1A1A1A"

    const val fontWeightBold = "700"

    const val buttonFontSize = "14px"

    const val mainFontSize = "15px"

    const val headerFontSize = "20px"

    val tooltipFontSize = Dimension(12.0, Dimension.LinearUnits.px)

    const val buttonStyle =
        "-fx-font-family: 'Roboto Condensed'; -fx-font-size: $buttonFontSize;" +
            " -fx-font-weight: 700; -fx-text-fill: #78909C;"

    const val navigationRailTabSize = 70.0

    const val FabRadius = 28.0

    const val tabStyle =
        "-fx-font-family: $font; -fx-font-weight:700; -fx-font-size: $buttonFontSize; " +
            "-fx-text-fill: $primaryColorLight; -fx-background-radius: 36"

    fun circleForRipple(button: MFXButton) = Circle(button.layoutX + 36.0, button.layoutY + 36.0, 35.0)

    fun circleForRipple(button: ToggleButton) = Circle(button.layoutX + 36.0, button.layoutY + 36.0, 35.0)
}
