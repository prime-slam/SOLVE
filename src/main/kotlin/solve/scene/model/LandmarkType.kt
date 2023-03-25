package solve.scene.model

enum class LandmarkType {
    Keypoint,
    Line,
    Plane;
    
    fun isShapeType(): Boolean = when (this) {
        Keypoint -> true
        Line -> true
        Plane -> false
    }
}
