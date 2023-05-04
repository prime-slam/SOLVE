package solve.unit.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import solve.utils.DelayedUpdatesManager
import solve.utils.Updatable

internal class DelayedUpdatesManagerTests {
    private class TestUpdatable(private val updateAction: (Int) -> Unit): Updatable<Int> {
        override fun update(data: Int) {
            updateAction(data)
        }
    }

    @Test
    fun `Doesn't update when was locked`() {
        val updatesManager = DelayedUpdatesManager<TestUpdatable, Int>()
        var lastUsedUpdateParameter: Int? = null
        val updatable = TestUpdatable { param -> lastUsedUpdateParameter = param }
        updatesManager.doLockedAction {
            updatesManager.delayUpdate(updatable, 1)
            updatesManager.delayUpdate(updatable, 2)
            assertNull(lastUsedUpdateParameter)
        }
    }

    @Test
    fun `Performs updates when unlocked`() {
        val updatesManager = DelayedUpdatesManager<TestUpdatable, Int>()
        val usedUpdateParams = mutableListOf<Int>()
        val updatable1 = TestUpdatable { param -> usedUpdateParams.add(param) }
        val updatable2 = TestUpdatable { param -> usedUpdateParams.add(param) }
        updatesManager.doLockedAction {
            updatesManager.delayUpdate(updatable1, 1)
            updatesManager.delayUpdate(updatable2, 2)
        }
        assertEquals(2, usedUpdateParams.size)
        assertTrue(usedUpdateParams.contains(1))
        assertTrue(usedUpdateParams.contains(2))
    }

    @Test
    fun `Performs only the last update for the element`() {
        val updatesManager = DelayedUpdatesManager<TestUpdatable, Int>()
        val usedUpdateParams = mutableListOf<Int>()
        val updatable = TestUpdatable { param -> usedUpdateParams.add(param) }
        updatesManager.doLockedAction {
            updatesManager.delayUpdate(updatable, 1)
            updatesManager.delayUpdate(updatable, 2)
        }
        assertEquals(1, usedUpdateParams.size)
        assertEquals(2, usedUpdateParams[0])
    }
}
