package solve.sidepanel.content

import javafx.scene.Node
import solve.main.MainController
import solve.main.splitpane.SidePanelLocation
import tornadofx.Controller
import tornadofx.*

class SidePanelContentController : Controller() {
    private val location: SidePanelLocation by param()

    private val view = find(SidePanelContentView::class, scope)
    private val mainController: MainController by inject(FX.defaultScope)

    private var isContentShowing = true

    fun showContent(contentNode: Node) {
        if (!isContentShowing) {
            mainController.showSidePanelContent(location)
            isContentShowing = true
        }
        view.showContent(contentNode)
    }

    fun clearContent() {
        view.clearContent()
        mainController.hideSidePanelContent(location)
        isContentShowing = false
    }
}
