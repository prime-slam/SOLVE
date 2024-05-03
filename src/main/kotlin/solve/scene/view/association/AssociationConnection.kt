package solve.scene.view.association

data class AssociationConnection(
    val firstFrameIndex: Int,
    val secondFrameIndex: Int,
    val keypointLayerIndex: Int,
    val associationLines: List<AssociationLine>
)
