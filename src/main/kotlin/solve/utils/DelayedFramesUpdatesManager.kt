package solve.utils

interface Updatable<T> {
    fun update(data: T)
}

/**
 * Global locker object, issued to delay all updates of FrameView until zoom action will be finished.
 * FrameView shouldn't update itself during zoom action
 * because it performs scroll to the left top corner and back and provokes redundant updates, which
 * affects performance.
 *
 * @param T type of objects, which updates can be delayed, FrameView in our case
 * @param S type of parameters to update objects with
 */
open class DelayedUpdatesManager<T : Updatable<S>, S> {
    private val delayedUpdates = mutableMapOf<T, S>()

    /**
     * FrameView should delay updates when this flag is set.
     */
    var shouldDelay = false
        private set

    /**
     * Zoom action should be passed here.
     */
    fun doLockedAction(action: () -> Unit) {
        shouldDelay = true
        action()
        shouldDelay = false
        delayedUpdates.forEach { (element, newData) ->
            element.update(newData)
        }
        delayedUpdates.clear()
    }

    fun delayUpdate(element: T, newData: S) {
        delayedUpdates[element] = newData
    }
}
