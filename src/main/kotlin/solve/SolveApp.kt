package solve

import solve.main.MainView
import javafx.stage.Stage
import tornadofx.*

class SolveApp : App(MainView::class) {
    override fun start(stage: Stage) {
        with(stage) {
            width = 1000.0
            height = 600.0
            isMaximized = true
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) = launch<SolveApp>(args)