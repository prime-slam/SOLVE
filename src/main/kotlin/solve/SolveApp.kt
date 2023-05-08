package solve

import javafx.stage.Stage
import solve.main.MainView
import solve.scene.view.landmarks.AnimationProvider
import solve.scene.view.landmarks.JavaFXAnimationProvider
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
        registerServices()
        super.start(stage)
    }

    private fun registerServices() {
        ServiceLocator.registerService<AnimationProvider>(JavaFXAnimationProvider())
    }
}

fun main(args: Array<String>) = launch<SolveApp>(args)
