package solve.main

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.SplitPane
import solve.utils.addAll
import tornadofx.onChange

open class FixedSplitPane(
    private val dividersInitialPositions: List<Double>,
    private val containedNodes: List<Node>
): SplitPane() {
    private var isWindowResizing = false
    private var isWindowResizingDetectionInitialized = false

    protected val dividersInstalledPositions = mutableMapOf<Divider, Double>()

    init {
        if (dividersInitialPositions.count() != containedNodes.count() - 1) {
            throw IllegalArgumentException(
                "A number of contained nodes does not corresponds to a number of divider positions!"
            )
        }
        if (dividersInitialPositions.any { it !in 0.0..1.0 }) {
            throw IllegalArgumentException("A divider position must be between 0.0 and 1.0!")
        }

        initializeContainingNodes()
        initializeDividers()
    }

    protected fun initializeDividerPositionControl(divider: Divider) {
        divider.positionProperty().onChange {
            if (!isWindowResizingDetectionInitialized) {
                initializeWindowResizingDetection()
                isWindowResizingDetectionInitialized = true
                return@onChange
            }

            if (!isWindowResizing) {
                dividersInstalledPositions[divider] = divider.position
            }
            dividersInstalledPositions[divider]?.let { divider.position = it }
        }
    }

    private fun initializeContainingNodes() {
        addAll(containedNodes)
        containedNodes.forEach {
            setResizableWithParent(it, false)
        }
    }

    private fun initializeDividers() {
        dividers.forEachIndexed { index, divider ->
            val initialPosition = dividersInitialPositions[index]
            divider.position = initialPosition
            dividersInstalledPositions[divider] = initialPosition
            initializeDividerPositionControl(divider)
        }
    }

    private fun initializeWindowResizingDetection() {
        scene.addPreLayoutPulseListener {
            isWindowResizing = true
            Platform.runLater {
                isWindowResizing = false
            }
        }
    }
}
