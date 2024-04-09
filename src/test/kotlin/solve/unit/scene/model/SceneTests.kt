package solve.unit.scene.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import solve.scene.model.ColorManager
import solve.scene.model.LayerSettings
import solve.scene.model.Scene

internal class SceneTests {
    private val colorManager = ColorManager<String>()
    private val pointLayerSettings1Name = "pointLayer1"
    private val pointLayerSettings1 =
        LayerSettings.PointLayerSettings(pointLayerSettings1Name, pointLayerSettings1Name, colorManager)
    private val pointLayerSettings2Name = "pointLayer2"
    private val pointLayerSettings2 =
        LayerSettings.PointLayerSettings(pointLayerSettings2Name, pointLayerSettings2Name, colorManager)
    private val planeLayerSettings1Name = "planeLayer1"
    private val planeLayerSettings1 =
        LayerSettings.PlaneLayerSettings(planeLayerSettings1Name, planeLayerSettings1Name, colorManager)
    private val planeLayerSettings2Name = "planeLayer2"
    private val planeLayerSettings2 =
        LayerSettings.PlaneLayerSettings(planeLayerSettings2Name, planeLayerSettings2Name, colorManager)

    @Test
    fun `Changes layer settings if index is correct`() {
        val orderManager = Scene(listOf(), listOf(pointLayerSettings1, pointLayerSettings2))
        assertEquals(0, orderManager.indexOf(pointLayerSettings1))
        assertEquals(1, orderManager.indexOf(pointLayerSettings2))
        orderManager.changeLayerIndex(pointLayerSettings1, 1)
        assertEquals(1, orderManager.indexOf(pointLayerSettings1))
        assertEquals(0, orderManager.indexOf(pointLayerSettings2))
        orderManager.changeLayerIndex(pointLayerSettings1, 1)
        assertEquals(1, orderManager.indexOf(pointLayerSettings1))
        assertEquals(0, orderManager.indexOf(pointLayerSettings2))
    }

    @Test
    fun `Changes layer settings if index is not correct`() {
        val orderManager = Scene(listOf(), listOf(pointLayerSettings1, pointLayerSettings2))
        assertThrows<IllegalStateException> { orderManager.changeLayerIndex(pointLayerSettings1, 10) }
        assertThrows<IllegalStateException> { orderManager.changeLayerIndex(pointLayerSettings1, -1) }
    }

    @Test
    fun `Runs order changed callback`() {
        val orderManager = Scene(listOf(), listOf(pointLayerSettings1, pointLayerSettings2))
        var callbackRunCounter = 0
        val callback: () -> Unit = { callbackRunCounter++ }
        orderManager.addOrderChangedListener(callback)
        orderManager.changeLayerIndex(pointLayerSettings1, 1)
        assertEquals(1, callbackRunCounter)
        orderManager.changeLayerIndex(pointLayerSettings1, 0)
        assertEquals(2, callbackRunCounter)
    }

    @Test
    fun `Remove order changed callback`() {
        val orderManager = Scene(listOf(), listOf(pointLayerSettings1, pointLayerSettings2))
        var callbackRunCounter = 0
        val callback: () -> Unit = { callbackRunCounter++ }
        orderManager.addOrderChangedListener(callback)
        orderManager.removeOrderChangedListener(callback)
        orderManager.changeLayerIndex(pointLayerSettings1, 1)
        assertEquals(0, callbackRunCounter)
    }

    @Test
    fun `Canvas layers is always above than non-canvas layers`() {
        val orderManager = Scene(
            listOf(),
            listOf(pointLayerSettings1, pointLayerSettings2, planeLayerSettings1, planeLayerSettings2)
        )
        assertEquals(0, orderManager.indexOf(planeLayerSettings1))
        assertEquals(1, orderManager.indexOf(planeLayerSettings2))
        assertEquals(2, orderManager.indexOf(pointLayerSettings1))
        assertEquals(3, orderManager.indexOf(pointLayerSettings2))
    }

    @Test
    fun `Can't swap canvas and non-canvas layers`() {
        val orderManager = Scene(
            listOf(),
            listOf(pointLayerSettings1, pointLayerSettings2, planeLayerSettings1, planeLayerSettings2)
        )
        orderManager.changeLayerIndex(pointLayerSettings1, 1)
        assertEquals(3, orderManager.indexOf(pointLayerSettings1))
        assertEquals(2, orderManager.indexOf(pointLayerSettings2))
        assertThrows<IllegalStateException> { orderManager.changeLayerIndex(planeLayerSettings1, 2) }
    }
}
