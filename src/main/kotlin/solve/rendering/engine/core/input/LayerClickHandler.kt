package solve.rendering.engine.core.input

import org.joml.Vector2i
import solve.scene.model.Landmark

abstract class LayerClickHandler<T: Landmark> {
    // Returns the index of the clicked landmark, if there is one.
    // Otherwise returns -1.
    abstract fun indexOfClickedLandmark(landmarks: List<T>, clickedPixelCoordinate: Vector2i): Int
}
