package solve

import javafx.event.Event
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits
import org.junit.jupiter.api.Assertions.assertNull
import java.lang.ref.WeakReference

internal fun<T> testMemoryLeak(factory: () -> T, action: (T) -> Unit) {
    val weakReference = WeakReference(factory())
    action(weakReference.get()!!)
    System.gc()
    val item = weakReference.get()
    assertNull(item, "$item was not collected")
}

internal fun Node.fireMouseDragged(x: Double, y: Double, button: MouseButton) =
    this.fireMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, button)
internal fun Node.fireMousePressed(x: Double, y: Double, button: MouseButton) =
    this.fireMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, button)

internal fun Node.fireMouseReleased(x: Double, y: Double, button: MouseButton) =
    this.fireMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, button)

internal fun Node.fireScrollEvent(x: Double, y: Double) {
    Event.fireEvent(
        this,
        ScrollEvent(
            ScrollEvent.SCROLL,
            x, y,
            x, y,
            false, false, false, false,
            false, false,
            0.15, 0.15,
            1.0, 1.0,
            HorizontalTextScrollUnits.NONE, 1.0, ScrollEvent.VerticalTextScrollUnits.NONE, 1.0, 1, null
        )
    )
}

private fun Node.fireMouseEvent(kind: EventType<MouseEvent>, x: Double, y: Double, button: MouseButton) {
    Event.fireEvent(
        this,
        MouseEvent(
            kind,
            x, y, x, y, button, 1,
            false, false, false, false,
            true, false, false, false,
            false, false, null
        )
    )
}
