package solve.scene.model

data class Point(val x: Short, val y: Short)

fun createPairs(width: Short, height: Short): Iterable<Point> = (0 until height).flatMap { row ->
    (0 until width).map { column ->
        Point(
            column.toShort(), row.toShort()
        )
    }
}