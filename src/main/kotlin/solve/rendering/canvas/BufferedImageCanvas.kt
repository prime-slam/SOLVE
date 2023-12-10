package solve.rendering.canvas

import javafx.animation.AnimationTimer
import javafx.scene.layout.Pane
import java.awt.Color
import java.awt.Image
import java.util.Arrays
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

class BufferedImageCanvas : AnimationTimer(), TestCanvas {
    private val width = 1036.0
    private val height = 1015.0

    private val backgroundColorArray = IntArray(width.toInt() * height.toInt())

    private val bufferedImageView = BufferedImageView(width.toInt(), height.toInt())

    override val root = Pane(bufferedImageView)

    private var frameImages = mutableListOf<Image>()
    private var scale = 0f

    private var scaledFrameWidth = 0
    private var scaledFrameHeight = 0
    private var gridWidth = 0
    private var gridHeight = 0

    override fun drawFrames(testFramePath: String, scale: Float, gridWidth: Int, gridHeight: Int) {
        repeat(gridWidth * gridHeight) {
            frameImages.add(ImageIO.read(Path(testFramePath).toUri().toURL()))
        }
        this.scale = scale
        this.gridWidth = gridWidth
        this.gridHeight = gridHeight

        val frameImage = frameImages.first()

        this.scaledFrameWidth = (frameImage.getWidth(null) * scale.toDouble()).toInt()
        this.scaledFrameHeight = (frameImage.getHeight(null) * scale.toDouble()).toInt()

        Arrays.fill(backgroundColorArray, 0)

        start()
    }

    override fun handle(now: Long) {
        val graphics = bufferedImageView.graphics
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, width.toInt(), height.toInt())

        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                graphics.drawImage(
                    frameImages[y * gridWidth + x],
                    x * scaledFrameWidth + (300 * sin(5 * now / 1000000000f)).toInt(),
                    y * scaledFrameHeight + (300 * sin(5 * now / 1000000000f)).toInt(),
                    scaledFrameWidth,
                    scaledFrameHeight,
                    null
                )
            }
        }

        bufferedImageView.updateBuffer()
    }
}