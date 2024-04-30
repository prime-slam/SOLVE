package solve.main.splitpane

import javafx.scene.Node

class SidePanelSplitPane(
    dividersInitialPositions: List<Double>,
    private val containedNodes: List<Node>,
    private val panelsLocation: SidePanelLocation,
    private val initiallyDisplayingLocation: SidePanelLocation
) : FixedSplitPane(dividersInitialPositions, containedNodes) {
    private var isLeftSidePanelHidden = false
    private var isRightSidePanelHidden = false

    init {
        hideInitiallyNonDisplayingNodes()
    }

    fun hideNodeAt(location: SidePanelLocation) {
        if (!isSidePanelLocation(location)) {
            return
        }

        if (location == SidePanelLocation.Left && !isLeftSidePanelHidden) {
            items.removeAt(0)
            isLeftSidePanelHidden = true
        } else if (location == SidePanelLocation.Right && !isRightSidePanelHidden) {
            items.removeAt(items.count() - 1)
            isRightSidePanelHidden = true
        } else {
            return
        }

        reinitializeDividersPositions()
    }

    fun showNodeAt(location: SidePanelLocation) {
        if (!isSidePanelLocation(location)) {
            return
        }

        val showingNode = getSideNode(location)
        if (location == SidePanelLocation.Left && isLeftSidePanelHidden) {
            items.add(0, showingNode)
            isLeftSidePanelHidden = false
        } else if (location == SidePanelLocation.Right && isRightSidePanelHidden) {
            items.add(items.lastIndex + 1, showingNode)
            isRightSidePanelHidden = false
        } else {
            return
        }

        reinitializeDividersPositions()
    }

    private fun reinitializeDividersPositions() {
        val indexOffset = if (isLeftSidePanelHidden) 1 else 0

        dividers.forEachIndexed { index, divider ->
            reinitializeDividerPositionsControl(divider, index + indexOffset)
        }
    }

    private fun reinitializeDividerPositionsControl(divider: Divider, installedPositionIndex: Int) {
        if (!positionIndexInRange(installedPositionIndex)) {
            println("Installed position index is out of range!")
            return
        }

        val installedPosition = dividersInstalledPositions[installedPositionIndex]
        val notHiddenDividersIndices = getNotHiddenDividersIndices()
        if (installedPosition != null && notHiddenDividersIndices != null) {
            val rightDividersWithLessPosition = dividersInstalledPositions.filter { entry ->
                entry.key in notHiddenDividersIndices &&
                    entry.key > installedPositionIndex &&
                    entry.value <= installedPosition
            }
            val leftDividersWithGreaterPosition = dividersInstalledPositions.filter { entry ->
                entry.key in notHiddenDividersIndices &&
                    entry.key < installedPositionIndex &&
                    entry.value >= installedPosition
            }

            var setPosition = installedPosition
            if (leftDividersWithGreaterPosition.isNotEmpty()) {
                setPosition =
                    leftDividersWithGreaterPosition.maxOf { entry -> entry.value } - MinimalDividersPositionDifference
            }
            if (rightDividersWithLessPosition.isNotEmpty()) {
                setPosition =
                    rightDividersWithLessPosition.minOf { entry -> entry.value } + MinimalDividersPositionDifference
            }
            divider.position = setPosition
        }

        initializeDividerPositionControl(divider, installedPositionIndex)
    }

    private fun isSidePanelLocation(location: SidePanelLocation) =
        panelsLocation == SidePanelLocation.Both || panelsLocation == location

    private fun getNotHiddenDividersIndices(): IntRange? {
        val dividersCount = dividersInstalledPositions.count()
        val firstIndex = if (!isLeftSidePanelHidden) 0 else 1
        val lastIndex = if (!isRightSidePanelHidden) dividersCount - 1 else dividersCount - 2

        if (firstIndex > lastIndex) {
            return null
        }
        return firstIndex..lastIndex
    }

    private fun getSideNode(nodeLocation: SidePanelLocation) = when (nodeLocation) {
        SidePanelLocation.Left -> containedNodes.first()
        SidePanelLocation.Right -> containedNodes.last()
        else -> null
    }

    private fun hideInitiallyNonDisplayingNodes() {
        when (initiallyDisplayingLocation) {
            SidePanelLocation.Left -> hideNodeAt(SidePanelLocation.Right)
            SidePanelLocation.Right -> hideNodeAt(SidePanelLocation.Left)
            else -> return
        }
    }

    companion object {
        private const val MinimalDividersPositionDifference = 0.001
    }
}

enum class SidePanelLocation {
    Left,
    Right,
    Both
}
