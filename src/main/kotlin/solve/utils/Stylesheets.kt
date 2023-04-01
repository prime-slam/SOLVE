package solve.utils

import javafx.scene.paint.Color
import tornadofx.*

class TransparentScalingButtonStyle: Stylesheet() {
    init {
        button {
            backgroundColor += Color.TRANSPARENT

            and(hover) {
                imageView {
                    scale(HoveredButtonScale)
                }
            }
        }
    }

    companion object {
        private const val HoveredButtonScale = 1.15
    }
}
