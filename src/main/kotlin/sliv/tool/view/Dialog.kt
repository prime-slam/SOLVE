package sliv.tool.view

import javafx.geometry.Insets
import tornadofx.*

open class Dialog(text: String) : Fragment() {
    override val root = vbox {
        padding = Insets(50.0, 50.0, 0.0, 50.0)
        text(text)
        button("OK") {
            action {
                close()
            }
        }
    }

    init {
        title = "Dialog window"
    }
}