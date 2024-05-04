package solve.scene.view.association

import solve.scene.model.Landmark

data class AssociationLine(
    val keypointsUID: Long,
    val firstKeypointFrameIndex: Int,
    val secondKeypointFrameIndex: Int,
    val firstKeypoint: Landmark.Keypoint,
    val secondKeypoint: Landmark.Keypoint
)
