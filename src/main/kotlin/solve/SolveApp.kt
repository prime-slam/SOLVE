package solve

import solve.main.MainView
import javafx.stage.Stage
import solve.styles.ThemeController
import tornadofx.*

class SolveApp : App(MainView::class) {
    private val themeController: ThemeController by inject()

    override fun start(stage: Stage) {
        themeController.start()
        with(stage) {
            width = 1000.0
            height = 600.0
            isMaximized = true
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) = launch<SolveApp>(args)