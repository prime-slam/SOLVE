package solve.scene.model

/**
 * Ordered storage.
 *
 * @param T type of stored elements.
 */
interface OrderManager<T> {
    fun addOrderChangedListener(action: () -> Unit)

    fun removeOrderChangedListener(action: () -> Unit)

    fun indexOf(element: T): Int
}
