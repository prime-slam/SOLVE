package solve.utils

import javafx.scene.paint.Color
import tornadofx.CssBox
import tornadofx.Dimension

val DarkLightGrayColor: Color = Color.color(0.6647059, 0.6647059, 0.6647059)

fun createLinearUnitsBox(top: Double, right: Double, bottom: Double, left: Double, unitsType: Dimension.LinearUnits) =
    CssBox(
        Dimension(top, unitsType), Dimension(right, unitsType), Dimension(bottom, unitsType), Dimension(left, unitsType)
    )

fun createPxBox(top: Double, right: Double, bottom: Double, left: Double) =
    createLinearUnitsBox(top, right, bottom, left, Dimension.LinearUnits.px)

fun createPxBoxWithValue(value: Double) = createPxBox(value, value, value, value)

fun createPxValue(value: Double) = Dimension(value, Dimension.LinearUnits.px)

fun createDegValue(value: Double) = Dimension(value, Dimension.AngularUnits.deg)
