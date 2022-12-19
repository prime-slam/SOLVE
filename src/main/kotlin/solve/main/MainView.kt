package solve.main

import solve.catalogue.view.CatalogueView
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import solve.sidepanel.SidePanelView
import tornadofx.*

class MainView : View() {
    private val catalogueView: CatalogueView by inject()
    private val sidePanelView: SidePanelView by inject()

    override val root = borderpane {
        top<MenuBarView>()
        center<SceneView>()
        left {
            hbox {
                add(sidePanelView)
                add(catalogueView)
            }
        }
    }
}