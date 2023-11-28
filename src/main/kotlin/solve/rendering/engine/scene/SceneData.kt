package solve.rendering.engine.scene

import solve.rendering.engine.camera.Camera

data class SceneData(
    val sceneObjects: List<SceneObject>,
    val camera: Camera
)
