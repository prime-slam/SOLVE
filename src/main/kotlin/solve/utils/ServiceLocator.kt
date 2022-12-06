package solve.utils

object ServiceLocator {
    val services = mutableMapOf<Any, Any>()

    inline fun <reified T: Any> getService(): T? = services.getOrDefault(T::class, null) as T?

    inline fun <reified T: Any> registerService(service: T) {
        services[T::class] = service
    }

    inline fun <reified T: Any> removeService() = services.remove(T::class)
}
