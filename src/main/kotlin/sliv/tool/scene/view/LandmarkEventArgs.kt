package sliv.tool.scene.view

import sliv.tool.scene.model.Layer

data class LandmarkEventArgs(val uid: Long, val layer: Layer, val frameTimestamp: Long)