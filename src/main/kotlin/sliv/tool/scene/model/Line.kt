package sliv.tool.scene.model

class Line(uid: Long, layer: Layer, val startCoordinate: Point, val finishCoordinate: Point) : Landmark(uid, layer)