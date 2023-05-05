package solve

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.stage.Stage
import solve.main.MainView
import solve.scene.view.landmarks.AnimationProvider
import solve.scene.view.landmarks.JavaFXAnimationProvider
import solve.styles.ApplicationStylesheet
import solve.utils.SVGImageLoaderDimensionProvider
import solve.utils.ServiceLocator
import tornadofx.App
import tornadofx.FX.Companion.stylesheets
import tornadofx.launch

class SolveApp : App(MainView::class, ApplicationStylesheet::class) {
    override fun start(stage: Stage) {
        initializeDependencies()
        registerServices()

        with(stage) {
            width = 1000.0
            height = 600.0
            isMaximized = true
        }
        super.start(stage)
    }

    private fun registerServices() {
        ServiceLocator.registerService<AnimationProvider>(JavaFXAnimationProvider())
    }

    private fun initializeDependencies() {
        SvgImageLoaderFactory.install(SVGImageLoaderDimensionProvider())
        initializeStyle()
    }

    private fun initializeStyle() {
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed")
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed:wght@700")
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto")
    }
}

fun main(args: Array<String>) = launch<SolveApp>(args)
