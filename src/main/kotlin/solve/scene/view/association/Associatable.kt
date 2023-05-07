package solve.scene.view.association

import solve.scene.model.Point

/**
 * Implementations can be associated with AssociationsManager
 *
 * @see AssociationsManager
 */
interface Associatable {
    val coordinate: Point
    val uid: Long
}
