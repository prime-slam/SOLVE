package solve.engine.jade

class GameObject {
    private var name: String
    private var components: MutableList<Component>
    @JvmField
    var transform: Transform

    constructor(name: String) {
        this.name = name
        components = ArrayList()
        transform = Transform()
    }

    constructor(name: String, transform: Transform) {
        this.name = name
        components = ArrayList()
        this.transform = transform
    }

    fun <T : Component?> getComponent(componentClass: Class<T>): T? {
        for (c in components) {
            if (componentClass.isAssignableFrom(c.javaClass)) {
                try {
                    return componentClass.cast(c)
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                    assert(false) { "Error: Casting component." }
                }
            }
        }
        return null
    }

    fun <T : Component?> removeComponent(componentClass: Class<T>) {
        for (i in components.indices) {
            val c = components[i]
            if (componentClass.isAssignableFrom(c.javaClass)) {
                components.removeAt(i)
                return
            }
        }
    }

    fun addComponent(c: Component) {
        components.add(c)
        c.gameObject = this
    }

    fun update(dt: Float) {
        for (i in components.indices) {
            components[i].update(dt)
        }
    }

    fun start() {
        for (i in components.indices) {
            components[i].start()
        }
    }
}