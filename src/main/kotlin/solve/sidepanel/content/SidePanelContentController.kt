package solve.sidepanel.content

import javafx.scene.Node
import solve.main.MainController
import tornadofx.Controller

class SidePanelContentController : Controller() {
    private val view: SidePanelContentView by inject()
    private val mainController: MainController by inject()

    private var isContentShowing = true

    fun showContent(contentNode: Node) {
        if (!isContentShowing) {
            mainController.showSidePanelContent()
            isContentShowing = true
        }
        view.showContent(contentNode)
    }

    fun clearContent() {
        view.clearContent()
        mainController.hideSidePanelContent()
        isContentShowing = false
    }
}
