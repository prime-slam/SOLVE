package solve.sidepanel.content

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class SidePanelContentView: View() {
    private var currentContentNode: Node? = null
    private var contentParentNode: Node = vbox {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
    }

    override val root = contentParentNode as Parent

    fun showContent(contentNode: Node) {
        contentParentNode.getChildList()?.remove(currentContentNode)
        currentContentNode = contentNode
        contentParentNode.add(contentNode)
    }

    fun clearContent() {
        contentParentNode.getChildList()?.remove(currentContentNode)
        currentContentNode = null
    }
}
