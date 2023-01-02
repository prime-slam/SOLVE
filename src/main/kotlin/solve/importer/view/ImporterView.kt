package solve.importer.view

import javafx.geometry.Insets
import javafx.stage.Stage
import tornadofx.borderpane
import tornadofx.View
import tornadofx.getChildList

class ImporterView : View("Choose working directory") {

    private val buttonView: ButtonView by inject()

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            buttonView.root.children[1].getChildList()!![1].isDisable = true
        }
        (root.scene.window as Stage).minWidth = 390.0

    }

    override val root = borderpane {
        padding = Insets(8.0, 10.0, 8.0, 10.0)
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ButtonView>()
    }
}