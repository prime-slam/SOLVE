package solve.scene.view

import javafx.scene.paint.Color

// Node with a lower viewOrder will be in front of a child with a higher viewOrder
const val IMAGE_VIEW_ORDER = Int.MAX_VALUE.toDouble()
const val LANDMARKS_VIEW_ORDER = Int.MAX_VALUE.toDouble() - 1
const val HIGHLIGHTING_VIEW_ORDER_GAP = Int.MAX_VALUE.toDouble() / 2

val NULL_COLOR = Color(0.0, 0.0, 0.0, 0.0)