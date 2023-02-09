package solve.sidepanel.content

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Priority
import solve.utils.clearChildren
import tornadofx.*

class SidePanelContentView: View() {
    private var contentParentNode: Node = vbox {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
    }

    override val root = contentParentNode as Parent

    fun showContent(contentNode: Node) {
        contentParentNode.replaceChildren(contentNode)
    }

    fun clearContent() {
        contentParentNode.clearChildren()
    }
}
