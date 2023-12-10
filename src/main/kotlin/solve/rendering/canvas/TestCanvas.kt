package solve.rendering.canvas

import javafx.scene.Parent

interface TestCanvas {
    val root: Parent

    fun drawFrames(testFramePath: String, scale: Float, gridWidth: Int, gridHeight: Int)

    companion object {
        const val FirstSkippedMeasurementsNumber = 10
        const val MeasurementsNumber = 100
    }
}