package solve.scene.view.rendering

abstract class TestCanvas {
    companion object {
        const val TestMeasurementsNumber = 1000
        const val InitialUnaccountedMeasurementsNumber = 200
    }

    abstract fun initCanvas()
}