package solve.utils

import javafx.geometry.Insets
import javafx.scene.layout.BackgroundSize
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.pow

val DarkLightGrayColor: Color = Color.web("#AAAAAA")

fun <T> createCssBoxWithValue(value: T) = CssBox(value, value, value, value)

fun createLinearUnitsBox(top: Double, right: Double, bottom: Double, left: Double, unitsType: Dimension.LinearUnits) =
    CssBox(
        Dimension(top, unitsType),
        Dimension(right, unitsType),
        Dimension(bottom, unitsType),
        Dimension(left, unitsType)
    )

fun createLinearUnitsBoxWithLeft(left: Double, unitsType: Dimension.LinearUnits) =
    createLinearUnitsBox(0.0, 0.0, 0.0, left, unitsType)

fun createPxValue(value: Double) = Dimension(value, Dimension.LinearUnits.px)

fun createPxBox(top: Double, right: Double, bottom: Double, left: Double) =
    createLinearUnitsBox(top, right, bottom, left, Dimension.LinearUnits.px)

fun createPxBoxWithValue(value: Double) = createPxBox(value, value, value, value)

fun PropertyHolder.scale(value: Double) {
    scaleX = value
    scaleY = value
}

fun createHGrowHBox() = HBox().also { it.hgrow = Priority.ALWAYS }

fun createVGrowBox() = VBox().also { it.vgrow = Priority.ALWAYS }

fun createInsetsWithValue(value: Double) = Insets(value, value, value, value)

// Returns black or white color according to which one is more contrasting with given color.
fun getBlackOrWhiteContrastingTo(color: Color): Color {
    val gamma = 2.2
    val redCoefficient = 0.2126
    val greenCoefficient = 0.7152
    val blueCoefficient = 0.0722
    val boundaryCoefficient = 0.5

    val contrastValue = redCoefficient * color.red.pow(gamma) +
        greenCoefficient * color.green.pow(gamma)
    blueCoefficient * color.blue.pow(gamma)

    val boundaryValue = boundaryCoefficient.pow(gamma)

    if (contrastValue <= boundaryValue) {
        return Color.WHITE
    }
    return Color.BLACK
}

fun Color.withReplacedOpacity(opacity: Double): Color = Color(red, green, blue, opacity)

fun CssSelectionBlock.addBackgroundImage(path: String) {
    backgroundImage += getResource(path)?.toURI() ?: return
    backgroundSize += BackgroundSize(
        100.0,
        100.0,
        true,
        true,
        true,
        true
    )
}
