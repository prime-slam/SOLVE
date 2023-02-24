package solve.utils.structures

data class Point(val x: Double, val y: Double) {
    operator fun unaryMinus() = Point(-x, -y)

    operator fun plus(otherPoint: Point) = Point(x + otherPoint.x, y + otherPoint.y)

    operator fun minus(otherPoint: Point) = this + (-otherPoint)
}
