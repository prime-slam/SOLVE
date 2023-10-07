package solve.scene.view.rendering.engine.util

import solve.scene.view.rendering.engine.components.Spritesheet
import solve.scene.view.rendering.engine.renderer.Shader
import solve.scene.view.rendering.engine.renderer.Texture
import java.io.File

object AssetPool {
    private val shaders: MutableMap<String, Shader> = HashMap()
    private val textures: MutableMap<String, Texture> = HashMap()
    private val spritesheets: MutableMap<String, Spritesheet?> = HashMap()
    fun getShader(resourceName: String): Shader? {
        val file = File(resourceName)
        return if (shaders.containsKey(file.absolutePath)) {
            shaders[file.absolutePath]
        } else {
            val shader = Shader(resourceName)
            shader.compile()
            shaders[file.absolutePath] = shader
            shader
        }
    }

    fun getTexture(resourceName: String): Texture? {
        val file = File(resourceName)
        return if (textures.containsKey(file.absolutePath)) {
            textures[file.absolutePath]
        } else {
            val texture = Texture(resourceName)
            textures[file.absolutePath] = texture
            texture
        }
    }

    fun addSpritesheet(resourceName: String?, spritesheet: Spritesheet?) {
        val file = File(resourceName)
        if (!spritesheets.containsKey(file.absolutePath)) {
            spritesheets[file.absolutePath] = spritesheet
        }
    }

    fun getSpritesheet(resourceName: String): Spritesheet? {
        val file = File(resourceName)
        if (!spritesheets.containsKey(file.absolutePath)) {
            assert(false) { "Error: Tried to access spritesheet '$resourceName' and it has not been added to asset pool." }
        }
        return spritesheets.getOrDefault(file.absolutePath, null)
    }
}