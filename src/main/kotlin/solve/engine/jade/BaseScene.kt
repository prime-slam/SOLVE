package solve.engine.jade

import solve.engine.components.SpriteRenderer
import solve.engine.components.Spritesheet
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL30
import solve.engine.util.AssetPool
import kotlin.math.cos
import kotlin.math.sin

class BaseScene : Scene() {
    private var time = 0f

    override fun init() {
        loadResources()
        camera = Camera(Vector2f(0f, 0f))
        val sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png")
        val background = GameObject("Background", Transform(Vector2f(), Vector2f(1920f, 1080f)))
        background.addComponent(SpriteRenderer(Vector4f(0.53f, 0.91f, 0.91f, 1f)))
        addGameObjectToScene(background)
        val sprite = sprites!!.getSprite(0)
        for (x in 0..500)
            for (y in 0..500) {
                val obj1 = GameObject("Object", Transform(Vector2f(
                    5f * x.toFloat(),
                    5f * y.toFloat()),
                    Vector2f(5f, 5f)))
                obj1.addComponent(SpriteRenderer(sprite))
                addGameObjectToScene(obj1)
            }
    }

    private fun loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl")
        AssetPool.addSpritesheet(
            "assets/images/spritesheet.png",
            Spritesheet(
                AssetPool.getTexture("assets/images/spritesheet.png"),
                16, 16, 26, 0
            )
        )
    }

    override fun update(dt: Float) {
        time += dt
        camera?.position?.x = 100 * sin(time)
        camera?.position?.y = 100 * cos(time)
        for (go in gameObjects) {
            go.update(dt)
        }
        renderer.render(this)
    }
}