package solve.main.splitpane

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.SplitPane
import solve.utils.addAll
import tornadofx.onChange

open class FixedSplitPane(
    private val dividersInitialPositions: List<Double>,
    private val containedNodes: List<Node>
) : SplitPane() {
    private var isWindowResizing = false
    protected val dividersInstalledPositions = mutableMapOf<Int, Double>()

    private var areDividersAbleToChange = false

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

    protected fun positionIndexInRange(positionIndex: Int) =
        positionIndex in 0 until dividersInstalledPositions.count()

    protected fun initializeDividerPositionControl(divider: Divider, installedPositionIndex: Int) {
        divider.positionProperty().onChange {
            if (!isWindowResizing && areDividersAbleToChange) {
                dividersInstalledPositions[installedPositionIndex] = divider.position
            } else {
                dividersInstalledPositions[installedPositionIndex]?.let { divider.position = it }
            }
        }

        Platform.runLater {
            initializeWindowResizingDetection()

            Platform.runLater {
                areDividersAbleToChange = true // Needed for a correct initialization of split pane dividers.
            }
        }
    }

    private fun initializeDividerPosition(divider: Divider, installedPositionIndex: Int) {
        if (!positionIndexInRange(installedPositionIndex)) {
            println("Installed position index is out of range!")
            return
        }
        dividersInstalledPositions[installedPositionIndex]?.let { divider.position = it }

        initializeDividerPositionControl(divider, installedPositionIndex)
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
            dividersInstalledPositions[index] = initialPosition

            initializeDividerPosition(divider, index)
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
