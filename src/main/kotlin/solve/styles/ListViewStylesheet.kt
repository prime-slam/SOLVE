package solve.styles

import javafx.scene.paint.Color
import tornadofx.*

class ListViewStylesheet : Stylesheet() {
    init {
        listView {
            backgroundColor += Color.valueOf(Style.BackgroundColor)
        }
        listCell {
            backgroundColor += Color.valueOf(Style.SurfaceColor)
        }

        cell {
            textFill = Color.valueOf(Style.OnBackgroundColor)
        }
    }
}
