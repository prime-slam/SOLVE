package solve.sidepanel

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import javafx.geometry.Insets
import solve.constants.*
import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import solve.main.MainView
import solve.utils.*
import tornadofx.*

class LeftPanelView : View() {

    val importer = find<ImporterView>()

    var content = MFXGenericDialog()
    var dialog = MFXStageDialog()

    val controller: ImporterController by inject()

//    private val importIcon = loadResourcesImage(IconsImportFAB)
//    private val projectIcon = loadResourcesImage(IconsProject)
//    private val pluginsIcon = loadResourcesImage(IconsPlugins)
//    private val settingsIcon = loadResourcesImage(IconsSettings)
//    private val helpIcon = loadResourcesImage(IconsHelp)



    override val root = vbox(7) {

//        addStylesheet(MFXButtonStyleSheet::class)
//
//        prefWidth = navigationRailSize
//
//        style = "-fx-background-color: ${Style.surfaceColor}"
//        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed")
//        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed:wght@700")
//        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700;900")
//
//
//
//
////        label("SOLVE"){
////            style = "-fx-font-family: ${Style.font}; -fx-font-weight:700; -fx-font-size: 18px"
////            VBox.setMargin(this, Insets(12.0, 6.0,0.0,6.0))
////
////
////        }
//        add(importFAB)
////        add(leftSidePanelViews.tabsView.root)
//
//
////        add(projectButton)
////        add(pluginsButton)
////        add(settingsButton)
////        add(helpButton)
//
//
////        button("Import project") {
////            action {
////                controller.directoryPath.set(null)
////                controller.projectAfterPartialParsing.set(null)
////                content = MaterialFXDialog.createGenericDialog(ImporterView().root)
////                dialog = MaterialFXDialog.createStageDialog(content, find<MainView>().currentStage, find<MainView>().root)
////                dialog.show()
////                content.padding = Insets(0.0,0.0,10.0,0.0)
////            }
////        }
////        button("Manage plugins") {
////        }
////        button("Settings") {
////        }
////        button("Help") {
////        }
    }



    fun importAction(){
        controller.directoryPath.set(null)
                controller.projectAfterPartialParsing.set(null)
                content = MaterialFXDialog.createGenericDialog(ImporterView().root)
                dialog = MaterialFXDialog.createStageDialog(content, find<MainView>().currentStage, find<MainView>().root)
                dialog.show()
                content.padding = Insets(0.0,0.0,10.0,0.0)
    }

}
