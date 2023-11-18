package solve.rendering.engine.rendering.texture

import java.nio.ByteBuffer

class Texture2DData(
    val data: ByteBuffer,
    val width: Int,
    val height: Int,
    val channelsType: TextureChannelsType
)
