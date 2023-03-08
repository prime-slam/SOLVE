package solve.utils

import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

val DarkLightGrayColor: Color = Color.web("#AAAAAA")

fun createLinearUnitsBox(top: Double, right: Double, bottom: Double, left: Double, unitsType: Dimension.LinearUnits) =
    CssBox(
        Dimension(top, unitsType), Dimension(right, unitsType), Dimension(bottom, unitsType), Dimension(left, unitsType)
    )

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
