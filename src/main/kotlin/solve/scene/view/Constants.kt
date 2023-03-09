package solve.scene.view

// Node with a lower viewOrder will be in front of a child with a higher viewOrder
const val IMAGE_VIEW_ORDER = Int.MAX_VALUE.toDouble()
const val LANDMARKS_VIEW_ORDER = Int.MAX_VALUE.toDouble() - 1
const val HIGHLIGHTING_VIEW_ORDER_GAP = Int.MAX_VALUE.toDouble() / 2
