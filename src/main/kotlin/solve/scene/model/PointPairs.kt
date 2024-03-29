package solve.scene.model

/**
 * Creates cartesian set of points by given size.
 * Creates only one instance for each size.
 */
object PointPairs {
    private val createdPairs = mutableMapOf<Size, List<Point>>()

    fun getPairs(size: Size): List<Point> = createdPairs[size] ?: createPairs(size).also { createdPairs[size] = it }

    private fun createPairs(size: Size) = (0 until size.height).flatMap { row ->
        (0 until size.width).map { column ->
            Point(
                column.toShort(),
                row.toShort()
            )
        }
    }
}
