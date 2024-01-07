package solve.rendering.engine.scene

import solve.rendering.engine.camera.Camera

data class SceneData(
    val renderObjects: List<RenderObject>,
    val camera: Camera
)
