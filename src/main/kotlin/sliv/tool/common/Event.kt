package sliv.tool.common

class Event<T> {
    private val listeners = mutableSetOf<(T) -> Unit>()

    operator fun plusAssign(observer: (T) -> Unit) {
        synchronized(this) {
            listeners.add(observer)
        }
    }

    operator fun minusAssign(observer: (T) -> Unit) {
        synchronized(this) {
            listeners.remove(observer)
        }
    }

    operator fun invoke(eventArgs: T) = listeners.forEach { observer -> observer(eventArgs) }
}