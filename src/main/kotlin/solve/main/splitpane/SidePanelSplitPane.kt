package solve.main.splitpane

import javafx.scene.Node

class SidePanelSplitPane(
    dividersInitialPositions: List<Double>,
    private val containedNodes: List<Node>,
    private val sidePanelLocation: SidePanelLocation
): FixedSplitPane(dividersInitialPositions, containedNodes)  {
    private var lastRemovedDivider: Divider? = null

    fun hideNode(node: Node) {
        if (!isSidePanelNode(node))
            return

        val dividersBeforeHiding = dividers.toSet()
        items.remove(node)
        lastRemovedDivider = dividersBeforeHiding.minus(dividers).first()
    }

    fun showNode(node: Node) {
        if (!isSidePanelNode(node) ||
            items.contains(node)
        ) {
            return
        }

        val nodeIndex = containedNodes.indexOf(node)

        val dividersBeforeAdding = dividers.toSet()
        items.add(nodeIndex, node)
        val addedDivider = dividers.minus(dividersBeforeAdding).first()

        initializeAddedDivider(addedDivider)
    }

    private fun initializeAddedDivider(addedDivider: Divider) {
        dividersInstalledPositions[addedDivider] = dividersInstalledPositions[lastRemovedDivider] ?: 0.0
        dividersInstalledPositions.remove(lastRemovedDivider)
        addedDivider.position = dividersInstalledPositions[addedDivider] ?: 0.0
        initializeDividerPositionControl(addedDivider)
    }

    private fun isSidePanelNode(node: Node): Boolean {
        return when (sidePanelLocation) {
            SidePanelLocation.Left -> node == containedNodes.first()
            SidePanelLocation.Right -> node == containedNodes.last()
            SidePanelLocation.Both -> node == containedNodes.first() || node == containedNodes.last()
        }
    }
}

enum class SidePanelLocation {
    Left,
    Right,
    Both
}
