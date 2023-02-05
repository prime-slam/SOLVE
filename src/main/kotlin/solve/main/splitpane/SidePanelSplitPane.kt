package solve.main.splitpane

import javafx.scene.Node

class SidePanelSplitPane(
    dividersInitialPositions: List<Double>,
    private val containedNodes: List<Node>,
    private val panelsLocation: SidePanelLocation
): FixedSplitPane(dividersInitialPositions, containedNodes)  {
    private var isLeftSidePanelHidden = false
    private var isRightSidePanelHidden = false

    fun hideNodeAt(location: SidePanelLocation) {
        if (!isSidePanelLocation(location)) {
            return
        }

        if (location == SidePanelLocation.Left && !isLeftSidePanelHidden) {
            items.removeFirst()
            isLeftSidePanelHidden = true
        } else if (location == SidePanelLocation.Right && !isRightSidePanelHidden) {
            items.removeLast()
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

        //val dividersBeforeAdding = dividers.toSet()
        if (location == SidePanelLocation.Left && isLeftSidePanelHidden) {
            items.add(0, containedNodes.first())
            isLeftSidePanelHidden = false
        } else if (location == SidePanelLocation.Right && isRightSidePanelHidden) {
            items.add(items.lastIndex + 1, containedNodes.last())
            isRightSidePanelHidden = false
        } else {
            return
        }

        //val addedDivider = dividers.minus(dividersBeforeAdding).first()
        //initializeDividerPositionControl(addedDivider, installedPositionIndex)
        reinitializeDividersPositions()
    }

    private fun reinitializeDividersPositions() {
        val indexOffset = if (isLeftSidePanelHidden) 1 else 0

        dividers.forEachIndexed { index, divider ->
            initializeDividerPositionControl(divider, index + indexOffset)
        }
    }

    private fun isSidePanelLocation(location: SidePanelLocation) =
        panelsLocation == SidePanelLocation.Both || panelsLocation == location
}

enum class SidePanelLocation {
    Left,
    Right,
    Both
}
