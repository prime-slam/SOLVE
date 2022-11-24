package sliv.tool.common

class Event<T> {
    private val listeners = mutableSetOf<(T) -> Unit>()

    fun clear() = listeners.clear()

    operator fun plusAssign(observer: (T) -> Unit) {
        listeners.add(observer)
    }

    operator fun minusAssign(observer: (T) -> Unit) {
        listeners.remove(observer)
    }

    operator fun invoke(eventArgs: T) = listeners.forEach { observer -> observer(eventArgs) }
}