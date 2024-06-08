package solve.rendering.engine.core.texture

import java.nio.ByteBuffer

/**
 * Used to store a texture data before it can be loaded to the GPU memory.
 */
class Texture2DData(
    val data: ByteBuffer,
    val width: Int,
    val height: Int,
    val channelsType: TextureChannelsType
)
