package solve.rendering.canvas

import javafx.animation.AnimationTimer
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import java.util.Arrays
import kotlin.math.pow
import kotlin.math.sqrt

class WritableImageCanvas : AnimationTimer(), TestCanvas {
    private val width = 1036.0
    private val height = 1015.0

    private val backgroundColorArray = IntArray(width.toInt() * height.toInt())

    private val writableImageView = WritableImageView(width.toInt(), height.toInt())

    override val root = Pane(writableImageView)

    private var frameImages = mutableListOf<Image>()
    private var scale = 0f

    private var scaledFrameWidth = 0
    private var scaledFrameHeight = 0
    private var gridWidth = 0
    private var gridHeight = 0

    private var previousTime = 0L

    private val measuredTimes = mutableListOf<Float>()
    private var measurementNumber = 0

    override fun drawFrames(testFramePath: String, scale: Float, gridWidth: Int, gridHeight: Int) {
        repeat(gridWidth * gridHeight) {
            frameImages.add(Image(testFramePath))
        }
        this.scale = scale
        this.gridWidth = gridWidth
        this.gridHeight = gridHeight

        val frameImage = frameImages.first()

        this.scaledFrameWidth = (frameImage.width * scale.toDouble()).toInt()
        this.scaledFrameHeight = (frameImage.height * scale.toDouble()).toInt()

        Arrays.fill(backgroundColorArray, 0)

        start()
    }

    override fun handle(now: Long) {
        writableImageView.updateBuffer()

        writableImageView.pixels = backgroundColorArray

        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                writableImageView.drawImage(
                    x * scaledFrameWidth,
                    y * scaledFrameHeight,
                    frameImages[y * gridWidth + x],
                    scaledFrameWidth,
                    scaledFrameHeight
                )
            }
        }

        writableImageView.updateBuffer()

        val deltaTime = (now - previousTime) / 1000000000f
        measurementNumber += 1

        if (measurementNumber > TestCanvas.FirstSkippedMeasurementsNumber)
            measuredTimes.add(deltaTime)

        if (measurementNumber == TestCanvas.FirstSkippedMeasurementsNumber + TestCanvas.MeasurementsNumber) {
            val avgTime = measuredTimes.average()
            val variance = measuredTimes.sumOf { (it - avgTime).pow(2) } / measuredTimes.count()
            val deviation = sqrt(variance)

            println("Average time: $avgTime")
            println("Deviation: $deviation")
        }

        previousTime = now
    }
}