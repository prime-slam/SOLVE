package solve.utils.structures

import kotlin.math.pow
import kotlin.math.sqrt

data class Point(val x: Double, val y: Double) {
    operator fun unaryMinus() = Point(-x, -y)

    operator fun plus(otherPoint: Point) = Point(x + otherPoint.x, y + otherPoint.y)

    operator fun minus(otherPoint: Point) = this + (-otherPoint)

    operator fun times(coefficient: Double) = Point(x * coefficient, y * coefficient)

    fun distanceTo(otherPoint: Point) = sqrt((otherPoint.x - x).pow(2) + (otherPoint.y - y).pow(2))
}
