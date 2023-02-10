package solve.main.splitpane

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
    private val dividersInstalledPositions = mutableMapOf<Int, Double>()

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

    protected fun initializeDividerPositionControl(divider: Divider, installedPositionIndex: Int) {
        if (installedPositionIndex !in 0 until dividersInstalledPositions.count()) {
            println("Installed position index is out of range!")
            return
        }
        dividersInstalledPositions[installedPositionIndex]?.let { divider.position = it }

        divider.positionProperty().onChange {
            if (!isWindowResizing) {
                dividersInstalledPositions[installedPositionIndex] = divider.position
            }
            dividersInstalledPositions[installedPositionIndex]?.let { divider.position = it }
        }

        Platform.runLater {
            initializeWindowResizingDetection()
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
            dividersInstalledPositions[index] = initialPosition
            initializeDividerPositionControl(divider, index)
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
