package sliv.tool.model.visualization

class Line(uid: Long, layer: Layer, val startCoordinate: Point, val finishCoordinate: Point) : Landmark(uid, layer) {
}