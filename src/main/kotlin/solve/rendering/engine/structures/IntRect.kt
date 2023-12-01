package solve.rendering.engine.structures

data class IntRect(val x0: Int, val y0: Int, val width: Int, val height: Int) {
    val x1: Int
        get() = x0 + width - 1
    val y1: Int
        get() = y0 + height - 1
}
