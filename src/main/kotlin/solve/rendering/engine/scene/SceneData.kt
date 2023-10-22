package solve.rendering.engine.scene

import solve.rendering.engine.camera.Camera

data class SceneData(
    val gameObjects: List<GameObject>,
    val camera: Camera
)
