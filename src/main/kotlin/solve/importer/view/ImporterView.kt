package solve.importer.view

import javafx.scene.layout.BorderPane
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import solve.importer.controller.ImporterController
import tornadofx.View

class ImporterView : View() {

    val importer = find<DirectoryPathView>()

    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }

    private val controller: ImporterController by inject()

    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override val root: BorderPane by fxml("/fxml/app.fxml", hasControllerAttribute = true)

    fun changeAction() {
        val dir = directoryChooser.showDialog(currentStage)
        controller.directoryPath.set(dir?.absolutePath)
    }

    fun cancelAction() {
        this.close()
    }
}
