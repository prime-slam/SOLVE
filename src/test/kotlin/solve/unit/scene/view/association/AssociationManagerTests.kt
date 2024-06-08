package solve.unit.scene.view.association

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import solve.scene.model.ColorManager
import solve.scene.model.Landmark
import solve.scene.model.Layer
import solve.scene.model.LayerSettings
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.AssociationManager
import kotlin.io.path.Path

internal class AssociationManagerTests {
    @Test
    fun `Associates two frames`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(0, 1)
        assert(associationManager.associationConnections.any {
            it.firstFrameIndex == 0 && it.secondFrameIndex == 1
        })
    }

    @Test
    fun `Associate three frames transitively`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(0, 1)
        associationManager.associate(1, 2)
        associationManager.associate(2, 0)
        assert(associationManager.associationConnections.any {
            it.firstFrameIndex == 0 && it.secondFrameIndex == 1
        } && associationManager.associationConnections.any {
            it.firstFrameIndex == 1 && it.secondFrameIndex == 2
        } && associationManager.associationConnections.any {
            it.firstFrameIndex == 2 && it.secondFrameIndex == 0
        })
    }

    @Test
    fun `Associates an existing frame with a non-existing frame`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(1, 10)
        assert(associationManager.associationConnections.isEmpty())
    }

    @Test
    fun `Associates frames a few times`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(0, 1)
        associationManager.associate(0, 1)
        assertEquals(1, associationManager.associationConnections.count())
        associationManager.associate(1, 0)
        assertEquals(1, associationManager.associationConnections.count())
    }

    @Test
    fun `Associates frames and clears association for the first frame`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(0, 1)
        assertEquals(1, associationManager.associationConnections.count())
        associationManager.clearAssociation(0)
        assertEquals(0, associationManager.associationConnections.count())
    }

    @Test
    fun `Associates frames and clears association for the second frame`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(0, 1)
        assertEquals(1, associationManager.associationConnections.count())
        associationManager.clearAssociation(1)
        assertEquals(0, associationManager.associationConnections.count())
    }

    @Test
    fun `Associates many-to-one frames and clears associations`() {
        val associationManager = AssociationManager()
        associationManager.setFramesSelection(testFrames)
        associationManager.associate(1, 0)
        associationManager.associate(2, 0)
        associationManager.associate(3, 0)
        associationManager.associate(4, 0)
        assertEquals(4, associationManager.associationConnections.count())
        associationManager.clearAssociation(0)
        assertEquals(0, associationManager.associationConnections.count())
    }

    companion object {
        val testPointLayers = listOf(
            Layer.PointLayer(
                "0",
                LayerSettings.PointLayerSettings("0", "0", ColorManager())
            ) { emptyList() },
            Layer.PointLayer(
                "1",
                LayerSettings.PointLayerSettings("1", "1", ColorManager())
            ) { emptyList() },
            Layer.PointLayer(
                "2",
                LayerSettings.PointLayerSettings("2", "2", ColorManager())
            ) { emptyList() },
            Layer.PointLayer(
                "3",
                LayerSettings.PointLayerSettings("3", "3", ColorManager())
            ) { emptyList() },
            Layer.PointLayer(
                "4",
                LayerSettings.PointLayerSettings("4", "4", ColorManager())
            ) { emptyList() }
        )
        val testFrames = listOf(
            VisualizationFrame(0L, Path("0.png"), listOf(testPointLayers[0])),
            VisualizationFrame(1L, Path("1.png"), listOf(testPointLayers[1])),
            VisualizationFrame(2L, Path("1.png"), listOf(testPointLayers[2])),
            VisualizationFrame(3L, Path("1.png"), listOf(testPointLayers[3])),
            VisualizationFrame(4L, Path("1.png"), listOf(testPointLayers[4]))
        )
    }
}