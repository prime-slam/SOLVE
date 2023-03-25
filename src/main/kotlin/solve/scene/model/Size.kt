package solve.scene.model

data class Size(val width: Int, val height: Int) {
    override fun hashCode(): Int {
        return width shl 16 + height
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        val otherSize = other as Size
        return width == otherSize.width && height == otherSize.height
    }
}