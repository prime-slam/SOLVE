package solve.rendering.engine.core.renderers

import solve.rendering.canvas.SceneCanvas
import solve.rendering.engine.Window
import solve.rendering.engine.utils.plus
import solve.rendering.engine.utils.toIntVector
import solve.scene.model.Landmark
import solve.scene.model.Layer
import solve.scene.model.Scene

/**
 * The base class for all landmarks renderers.
 * Encapsulates logic to provide virtualization at the data layer.
 */
abstract class LandmarkLayerRenderer(
    window: Window,
    protected val getScene: () -> Scene?
) : Renderer(window) {
    protected var framesRatio: Float = 1f

    private var _visibleLayers = emptyList<Layer>()
    protected val visibleLayers: List<Layer>
        get() = _visibleLayers

    private var _previousVisibleLayers = emptyList<Layer>()
    protected val previousVisibleLayers: List<Layer>
        get() = _previousVisibleLayers
    private var visibleLayersToLandmarksMap = mutableMapOf<Layer, List<Landmark>>()
    private var _visibleLayersLandmarks = emptyList<List<Landmark>>()

    protected val visibleLayersLandmarks: List<List<Landmark>>
        get() = _visibleLayersLandmarks
    private var _visibleLayersSelectionIndices = emptyList<Int>()

    protected val visibleLayersSelectionIndices: List<Int>
        get() = _visibleLayersSelectionIndices

    private var _hiddenVisibleLayersInCurrentFrame = setOf<Layer>()
    protected val hiddenVisibleLayersInCurrentFrame: Set<Layer>
        get() = _hiddenVisibleLayersInCurrentFrame

    private var _newVisibleLayersInCurrentFrame = setOf<Layer>()
    protected val newVisibleLayersInCurrentFrame: Set<Layer>
        get() = _newVisibleLayersInCurrentFrame

    protected var layers = emptyList<Layer>()

    fun setFramesSelectionLayers(layers: List<Layer>) {
        this.layers = layers
    }

    override fun onSceneFramesUpdated() {
        this.framesRatio = framesSize.x / framesSize.y
    }

    override fun beforeRender() {
        updateVisibleLayers()

        _hiddenVisibleLayersInCurrentFrame = _previousVisibleLayers.subtract(this._visibleLayers.toSet())
        _newVisibleLayersInCurrentFrame = this._visibleLayers.subtract(_previousVisibleLayers.toSet())

        _hiddenVisibleLayersInCurrentFrame.forEach { visibleLayersToLandmarksMap.remove(it) }
        _newVisibleLayersInCurrentFrame.forEach { visibleLayersToLandmarksMap[it] = it.getLandmarks() }
        _visibleLayersLandmarks = visibleLayersToLandmarksMap.keys.sortedBy {
            layers.indexOf(it)
        }.mapNotNull { visibleLayersToLandmarksMap[it] }
    }

    private fun updateVisibleLayers() {
        if (layers.isEmpty()) {
            return
        }

        val framesIntSize = framesSize.toIntVector()
        val windowTopLeftShaderPosition = window.calculateTopLeftCornerShaderPosition()
        val windowSize = window.size

        val windowTopLeftFramePosition = SceneCanvas.shaderToFrameVector(windowTopLeftShaderPosition, framesIntSize)
        val visibleFramesVector = SceneCanvas.screenToFrameVector(windowSize, framesIntSize, window)
        val windowDownRightFramePosition = windowTopLeftFramePosition + visibleFramesVector

        val topLeftVisibleFrameIndexCoordinates = windowTopLeftFramePosition.toIntVector()
        val downRightVisibleFrameIndexCoordinates = windowDownRightFramePosition.toIntVector()

        val newVisibleLayers = mutableListOf<Layer>()
        val newVisibleLayersSelectionIndices = mutableListOf<Int>()
        for (y in topLeftVisibleFrameIndexCoordinates.y..downRightVisibleFrameIndexCoordinates.y) {
            for (x in topLeftVisibleFrameIndexCoordinates.x..downRightVisibleFrameIndexCoordinates.x) {
                val layerIndex = y * gridWidth + x
                if (layerIndex !in layers.indices) {
                    continue
                }

                newVisibleLayers.add(layers[layerIndex])
                newVisibleLayersSelectionIndices.add(layerIndex)
            }
        }

        _previousVisibleLayers = this._visibleLayers
        this._visibleLayers = newVisibleLayers
        _visibleLayersSelectionIndices = newVisibleLayersSelectionIndices
    }
}
