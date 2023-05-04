package solve.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class CacheTests {
    private class TestCacheElement<T>(private val initAction: (T) -> Unit): CacheElement<T> {
        var name: String? = null
        override fun init(params: T) {
            initAction(params)
        }
    }

    @Test
    fun `Get data`() {
        val commonParams = 1
        val validate: (TestCacheElement<Int>) -> Boolean = { true }
        var lastUsedParams: Int? = null
        val cache = Cache(validate, commonParams) { params ->
            lastUsedParams = params
            TestCacheElement { }
        }

        val element1Params = 1
        val element1 = cache.get(element1Params)
        assertEquals(element1Params, lastUsedParams)

        val element2Params = 2
        val element2 = cache.get(element2Params)
        assertEquals(element2Params, lastUsedParams)

        assertNotSame(element1, element2)
    }

    @Test
    fun `Reuse data`() {
        val validate: (TestCacheElement<Int>) -> Boolean = { true }
        var lastUsedCreationParams: Int? = null
        var lastUsedReinitializationParams: Int? = null
        val factory: (Int) -> TestCacheElement<Int> = { params ->
            lastUsedCreationParams = params
            TestCacheElement { initParams ->
                lastUsedReinitializationParams = initParams
            }
        }
        val cache = Cache(validate, 1, factory)

        val createdElement1 = factory(1)
        val createdElement2 = factory(2)

        cache.store(createdElement1)
        cache.store(createdElement2)

        val element1Params = 1
        val element2Params = 2
        val element3Params = 3

        val element1 = cache.get(element1Params)
        assertEquals(element1Params, lastUsedReinitializationParams)
        val element2 = cache.get(element2Params)
        assertEquals(element2Params, lastUsedReinitializationParams)

        assertSame(createdElement1, element1)
        assertSame(createdElement2, element2)

        val element3 = cache.get(element3Params)
        assertEquals(element3Params, lastUsedCreationParams)
        assertEquals(element2Params, lastUsedReinitializationParams)

        assertNotSame(element1, element3)
        assertNotSame(element2, element3)
    }

    @Test
    fun `Validate element`() {
        val validate: (TestCacheElement<Int>) -> Boolean = { it.name != "break" }
        val cache = Cache(validate, 1) { TestCacheElement {  } }

        val element = TestCacheElement<Int> { }
        assertDoesNotThrow { cache.store(element) }

        val anotherElement = TestCacheElement<Int> { }
        anotherElement.name = "break"

        org.junit.jupiter.api.assertThrows<IllegalArgumentException> { cache.store(anotherElement) }
    }
}