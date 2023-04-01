package solve.sidepanel.content

import javafx.scene.Node
import solve.main.MainController
import solve.main.splitpane.SidePanelLocation
import tornadofx.Controller
import tornadofx.*

class SidePanelContentController : Controller() {
    private val location: SidePanelLocation by param()
    private val isContentShowingParam: Boolean by param()
    private var isContentShowing = isContentShowingParam

    private val view: SidePanelContentView by inject(scope)
    private val mainController: MainController by inject(FX.defaultScope)

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
