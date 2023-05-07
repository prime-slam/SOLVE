package solve.scene.view.association

import javafx.scene.layout.Pane

/**
 * A region where not in frames visual elements such as association adorners and lines is located.
 */
class OutOfFramesLayer : Pane() {
    init {
        // The element should pass all mouse event to the scene
        isMouseTransparent = true
    }
}
