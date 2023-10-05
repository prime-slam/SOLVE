package solve.engine.jade

import org.joml.Vector2f

class Transform {
    @JvmField
    var position: Vector2f? = null
    @JvmField
    var scale: Vector2f? = null

    constructor() {
        init(Vector2f(), Vector2f())
    }

    constructor(position: Vector2f?) {
        init(position, Vector2f())
    }

    constructor(position: Vector2f?, scale: Vector2f?) {
        init(position, scale)
    }

    fun init(position: Vector2f?, scale: Vector2f?) {
        this.position = position
        this.scale = scale
    }
}