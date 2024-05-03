package solve.scene.view.association

import solve.scene.model.Layer
import solve.scene.model.VisualizationFrame

class AssociationManager {
    private var framesSelection = listOf<VisualizationFrame>()
    private val _associationConnections = mutableListOf<AssociationConnection>()
    val associationConnections: List<AssociationConnection>
        get() = _associationConnections

    fun setFramesSelection(framesSelection: List<VisualizationFrame>) {
        this.framesSelection = framesSelection
        _associationConnections.clear()
    }

    fun associate(firstFrameIndex: Int, secondFrameIndex: Int, associatingKeypointLayerIndex: Int = 0) {
        if (firstFrameIndex !in framesSelection.indices || secondFrameIndex !in framesSelection.indices) {
            println("Associating frames indices is out of range!")
            return
        }

        val firstFrame = framesSelection[firstFrameIndex]
        val secondFrame = framesSelection[secondFrameIndex]
        val firstFrameKeypointLayers = firstFrame.layers.filterIsInstance<Layer.PointLayer>()
        val secondFramePointLayers = secondFrame.layers.filterIsInstance<Layer.PointLayer>()

        if (associatingKeypointLayerIndex !in firstFrameKeypointLayers.indices ||
            associatingKeypointLayerIndex !in secondFramePointLayers.indices) {
            println("There are no point layer with a given index to associate!")
            return
        }

        val firstFramePointLayer = firstFrameKeypointLayers[associatingKeypointLayerIndex]
        val secondFramePointLayer = secondFramePointLayers[associatingKeypointLayerIndex]
        val firstFrameKeypoints = firstFramePointLayer.getLandmarks().associateBy { it.uid }
        val secondFrameKeypoints = secondFramePointLayer.getLandmarks().associateBy { it.uid }

        val associationLines = mutableListOf<AssociationLine>()
        firstFrameKeypoints.keys.forEach { firstFrameKeypointUID ->
            if (secondFrameKeypoints.contains(firstFrameKeypointUID)) {
                val firstKeypoint = firstFrameKeypoints[firstFrameKeypointUID] ?: return@forEach
                val secondKeypoint = secondFrameKeypoints[firstFrameKeypointUID] ?: return@forEach

                associationLines.add(AssociationLine(
                        firstFrameKeypointUID,
                        firstFrameIndex,
                        secondFrameIndex,
                        firstKeypoint,
                        secondKeypoint
                    )
                )
            }
        }
        _associationConnections.add(
            AssociationConnection(
                firstFrameIndex, secondFrameIndex, associatingKeypointLayerIndex, associationLines)
        )
    }

    fun clearAssociation(frameIndex: Int, associatedKeypointLayerIndex: Int = 0) {
        _associationConnections.removeAll {
            it.keypointLayerIndex == associatedKeypointLayerIndex &&
            (it.firstFrameIndex == frameIndex || it.secondFrameIndex == frameIndex)
        }
    }
}