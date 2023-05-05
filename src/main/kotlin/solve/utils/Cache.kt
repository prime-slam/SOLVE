package solve.utils

interface Storage<T> {
    fun store(element: T)
}

/**
 * Element, that can be stored in the cache.
 */
interface CacheElement<T> {
    /**
     * Reinitialize element when it was reused by the cache.
     */
    fun init(params: T)
}

/**
 * Produces elements of the specified type. Also, can reuse stored elements.
 *
 * @param T type of stored elements.
 * @param S type of parameters of each element needed to produce new elements and reinitialize reused ones.
 * @param U type of global parameters of cache, used to invalidate the cache if needed.
 *
 * @param validate checks if element can be stored.
 * @param parameters global parameters of the cache, not related to elements.
 * @param factory function, that produces new elements with needed parameters
 */
class Cache<T : CacheElement<S>, S, U>(
    private val validate: (T) -> Boolean,
    val parameters: U,
    private val factory: (S) -> T
) : Storage<T> {
    private val storage = mutableSetOf<T>()

    /**
     * Returns stored and reinitialized element if it is possible, else produces new element with the specified params.
     */
    fun get(parameters: S) = storage.firstOrNull()?.also { element ->
        element.init(parameters)
        storage.remove(element)
    } ?: factory(parameters)

    override fun store(element: T) {
        if (!validate(element)) {
            throw IllegalArgumentException()
        }
        storage.add(element)
    }
}
