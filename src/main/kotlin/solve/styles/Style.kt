package solve.styles

import io.github.palexdev.materialfx.controls.MFXButton
import javafx.geometry.Insets
import javafx.scene.control.ToggleButton
import javafx.scene.shape.Circle
import tornadofx.*


object Style {
    const val BackgroundColor = "EFF0F0"

    const val SurfaceColor = "FFFFFF"

    const val TooltipColor = "707070"

    const val PrimaryColor = "78909C"

    const val OnBackgroundColor = "000000"

    const val PrimaryColorLight = "B0BEC5"

    const val SecondaryColor = "41497F"

    const val SeparatorLineColor = "9DAEB7"

    const val SettingLightColor = "EDEEF1"

    const val ErrorColor = "EF6E6B"

    const val ActiveColor = "41497F"

    const val FontCondensed = "'Roboto Condensed'"

    const val Font = "Roboto"

    const val ListFontColor = "3E4345"

    const val HeaderFontColor = "1A1A1A"

    val headerPadding = Insets(0.0, 0.0, 0.0, 24.0)

    const val FontWeightBold = "700"

    const val ButtonFontSize = "14px"

    const val MainFontSize = "15px"

    val TooltipFontSize = Dimension(12.0, Dimension.LinearUnits.px)

    const val ButtonStyle =
        "-fx-font-family: 'Roboto Condensed'; -fx-font-size: $ButtonFontSize;" +
            " -fx-font-weight: 700; -fx-text-fill: #78909C;"

    const val NavigationRailTabSize = 70.0

    const val FabRadius = 28.0

    const val tabStyle =
        "-fx-font-family: $Font; -fx-font-weight:700; -fx-font-size: $ButtonFontSize; " +
            "-fx-text-fill: $PrimaryColorLight; -fx-background-radius: 36"
    fun circleForRipple(button: MFXButton) = Circle(button.layoutX + 36.0, button.layoutY + 36.0,35.0)

    fun circleForRipple(button: ToggleButton) = Circle(button.layoutX + 36.0, button.layoutY + 36.0, 35.0)

    const val HeaderFontSize = "20px"

    const val ControlButtonsSpacing = 10.0
}
