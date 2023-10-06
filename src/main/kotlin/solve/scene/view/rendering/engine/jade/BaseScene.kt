package solve.scene.view.rendering.engine.jade

import solve.scene.view.rendering.engine.components.SpriteRenderer
import org.joml.Vector2f
import solve.scene.view.rendering.engine.components.Sprite
import solve.scene.view.rendering.engine.renderer.Texture
import solve.scene.view.rendering.engine.util.AssetPool
import kotlin.math.cos
import kotlin.math.sin

class BaseScene : Scene() {
    companion object {
        private const val TestSceneColumnsNumber = 100
        private const val TestSceneRowsNumber = 100
        private const val ImagesNumber = 39

        private const val ImageWidth = 1034f
        private const val ImageHeight = 768f

        private const val ImageScale = 0.02f

        private const val ImagesOffset = 5f
    }

    private var time = 0f
    val textures = mutableListOf<Texture>()

    private val imageWidth = ImageWidth * ImageScale
    private val imageHeight = ImageHeight * ImageScale
    private val sceneWidth = TestSceneColumnsNumber * imageWidth
    private val sceneHeight = TestSceneRowsNumber * imageHeight

    override fun init() {
        loadResources()

        camera = Camera(Vector2f())

        for (x in 0 until TestSceneColumnsNumber)
            for (y in 0 until TestSceneRowsNumber) {
                val textureIndex = (y * TestSceneColumnsNumber + x) % ImagesNumber
                val sprite = Sprite(textures[textureIndex])

                val spriteObject = GameObject("Object", Transform(Vector2f(
                     imageWidth * x.toFloat(),
                    imageHeight * y.toFloat()),
                    Vector2f(imageWidth + ImagesOffset, imageHeight + ImagesOffset))
                )
                spriteObject.addComponent(SpriteRenderer(sprite))
                addGameObjectToScene(spriteObject)
            }
    }

    private fun loadResources() {
        AssetPool.getShader("openGLResources/shaders/default.glsl")

        for (textureIndex in 1 .. ImagesNumber) {
            textures.add(Texture("openGLResources/images/$textureIndex.png"))
        }
    }

    override fun update(dt: Float) {
        time += dt
        camera?.position?.x = sceneWidth / 2.5f * sin(time)
        camera?.position?.y = sceneHeight / 3f + sceneHeight / 2.5f * cos(time)
        for (go in gameObjects) {
            go.update(dt)
        }
        renderer.render(this)
    }
}