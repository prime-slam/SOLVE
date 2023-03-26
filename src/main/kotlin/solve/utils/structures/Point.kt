package solve.utils.structures

import kotlin.math.pow
import kotlin.math.sqrt

data class Point(val x: Double, val y: Double) {
    operator fun unaryMinus() = Point(-x, -y)

    operator fun plus(otherPoint: Point) = Point(x + otherPoint.x, y + otherPoint.y)

    operator fun minus(otherPoint: Point) = this + (-otherPoint)

    fun distanceTo(otherPoint: Point): Double = sqrt((x - otherPoint.x).pow(2) + (y - otherPoint.y).pow(2))
}
