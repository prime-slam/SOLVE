package solve.utils.structures

import kotlin.math.pow
import kotlin.math.sqrt

data class DoublePoint(val x: Double, val y: Double) {
    operator fun unaryMinus() = DoublePoint(-x, -y)

    operator fun plus(otherPoint: DoublePoint) = DoublePoint(x + otherPoint.x, y + otherPoint.y)

    operator fun minus(otherPoint: DoublePoint) = this + (-otherPoint)

    operator fun times(coefficient: Double) = DoublePoint(x * coefficient, y * coefficient)

    fun distanceTo(otherPoint: DoublePoint): Double = sqrt((x - otherPoint.x).pow(2) + (y - otherPoint.y).pow(2))
}
