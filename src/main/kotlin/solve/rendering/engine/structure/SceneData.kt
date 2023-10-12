package solve.rendering.engine.structure

import solve.rendering.engine.camera.Camera

data class SceneData(
    val gameObjects: List<GameObject>,
    val camera: Camera
)
