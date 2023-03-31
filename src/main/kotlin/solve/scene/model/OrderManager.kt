package solve.scene.model

interface OrderManager<T> {
    fun addOrderChangedListener(action: () -> Unit)

    fun removeOrderChangedListener(action: () -> Unit)

    fun indexOf(element: T): Int
}
