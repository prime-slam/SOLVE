package sliv.tool.scene.view

class Event<T> {
    private val observers = mutableSetOf<(T) -> Unit>()

    operator fun plusAssign(observer: (T) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(eventArgs: T) {
        for (observer in observers)
            observer(eventArgs)
    }
}

data class LandmarkEventArgs(val uid: Long, val frameTimestamp: Long)

object FramesEventManager {
    val LandmarkSelected = Event<LandmarkEventArgs>()
    val LandmarkUnselected = Event<LandmarkEventArgs>()
}