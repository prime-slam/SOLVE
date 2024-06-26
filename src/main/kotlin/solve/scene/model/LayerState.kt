package solve.scene.model

import javafx.beans.property.FloatProperty
import solve.utils.PropertyTranslator
import tornadofx.*

private data class LandmarkHighlightingData(
    val highlightingProperty: FloatProperty,
    val propertyTranslator: PropertyTranslator<Number>
)

/**
 * Landmarks settings which can not be reused when scene is recreated
 */
class LayerState(val name: String) {
    private val _selectedLandmarksUIDs = mutableSetOf<Long>()
    private val _hoveredLandmarksUIDs = mutableSetOf<Long>()

    // Maps UID to the percentage of landmark highlighting progress.
    private val landmarksHighlightingProgress = mutableMapOf<Long, LandmarkHighlightingData>()

    val selectedLandmarksUIDs: Set<Long>
        get() = _selectedLandmarksUIDs
    val hoveredLandmarkUIDs: Set<Long>
        get() = _hoveredLandmarksUIDs

    fun selectLandmark(landmarkUID: Long) {
        _selectedLandmarksUIDs.add(landmarkUID)
        highlightLandmark(landmarkUID)
    }

    fun deselectLandmark(landmarkUID: Long) {
        if (landmarkUID !in _selectedLandmarksUIDs) {
            println("There is no landmark with given UID!")
            return
        }

        unhighlightLandmark(landmarkUID, onFinished = {
            _selectedLandmarksUIDs.remove(landmarkUID)
        })
    }

    fun hoverLandmark(landmarkUID: Long) {
        _hoveredLandmarksUIDs.add(landmarkUID)
        highlightLandmark(landmarkUID)
    }

    fun unhoverLandmark(landmarkUID: Long) {
        if (landmarkUID !in _hoveredLandmarksUIDs) {
            println("There is no landmark with given UID!")
            return
        }

        unhighlightLandmark(landmarkUID, onFinished = {
            _hoveredLandmarksUIDs.remove(landmarkUID)
            println("unhovered")
        })
    }

    fun getLandmarkHighlightingProgress(landmarkUID: Long): Float {
        return landmarksHighlightingProgress[landmarkUID]?.highlightingProperty?.value ?: 0f
    }

    private fun highlightLandmark(landmarkUID: Long, onFinished: () -> Unit = { }) {
        if (!landmarksHighlightingProgress.contains(landmarkUID)) {
            val highlightingProperty = floatProperty(0f)
            landmarksHighlightingProgress[landmarkUID] =
                LandmarkHighlightingData(highlightingProperty, PropertyTranslator(highlightingProperty))
        }

        landmarksHighlightingProgress[landmarkUID]?.propertyTranslator?.translateTo(
            1f,
            HighlightDurationMillis,
            onFinished
        )
    }

    private fun unhighlightLandmark(landmarkUID: Long, onFinished: () -> Unit = { }) {
        if (!landmarksHighlightingProgress.contains(landmarkUID)) {
            return
        }

        landmarksHighlightingProgress[landmarkUID]?.propertyTranslator?.translateTo(
            0f,
            HighlightDurationMillis,
            onFinished
        )
    }

    companion object {
        private const val HighlightDurationMillis = 300L
    }
}
