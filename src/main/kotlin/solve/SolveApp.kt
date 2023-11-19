package solve

import javafx.stage.Stage
import solve.main.MainView
import solve.utils.ServiceLocator
import tornadofx.App
import tornadofx.launch

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
