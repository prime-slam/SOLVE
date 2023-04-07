package solve.parsers.structures

data class Line(val uid: Long, val x0: Double, val y0: Double, val x1: Double, val y1: Double) {
    override fun toString() = "$uid,$x0,$y0,$x1,$y1"
}
