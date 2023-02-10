package solve.utils

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import tornadofx.CssBox
import tornadofx.Dimension
import tornadofx.PropertyHolder

val DarkLightGrayColor: Color = Color.web("#AAAAAA")

val DefaultGrayColor: Color = Color.web("#f4f4f4")

val FakeTransparentColor: Color = Color(1.0, 1.0, 1.0, 0.01)

fun createLinearUnitsBox(top: Double, right: Double, bottom: Double, left: Double, unitsType: Dimension.LinearUnits) =
    CssBox(
        Dimension(top, unitsType), Dimension(right, unitsType), Dimension(bottom, unitsType), Dimension(left, unitsType)
    )

fun createPxBox(top: Double, right: Double, bottom: Double, left: Double) =
    createLinearUnitsBox(top, right, bottom, left, Dimension.LinearUnits.px)

fun <T> createCssBoxWithValue(value: T) = CssBox(value, value, value, value)

fun createPxBoxWithValue(value: Double) = createPxBox(value, value, value, value)

fun createPxValue(value: Double) = Dimension(value, Dimension.LinearUnits.px)

fun createDegValue(value: Double) = Dimension(value, Dimension.AngularUnits.deg)

fun PropertyHolder.scale(value: Double) {
    scaleX = value
    scaleY = value
}

// Creates a fully transparent image that contains only one pixel with small non-transparency.
fun createFakeTransparentImage(width: Int, height: Int): Image? {
    if (width <= 0 || height <= 0) {
        println("Width and height of the image should be greater than zero!")
        return null
    }

    val writableImage = WritableImage(width, height)
    for (x in 0 until width) {
        for (y in 0 until height) {
            writableImage.pixelWriter.setColor(x, y, Color.TRANSPARENT)
        }
    }
    writableImage.pixelWriter.setColor(0, 0, FakeTransparentColor)

    return writableImage
}
