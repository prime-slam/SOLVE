package sliv.tool.scene.model

sealed class Landmark(val uid: Long, val layer: Layer) {
    class Keypoint(uid: Long, layer: Layer, val coordinate: Point) : Landmark(uid, layer)
    class Line(uid: Long, layer: Layer, val startCoordinate: Point, val finishCoordinate: Point) : Landmark(uid, layer)
    class Plane(uid: Long, layer: Layer, val points: Array<Point>) : Landmark(uid, layer)
}