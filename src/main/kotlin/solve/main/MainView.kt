package solve.main

import solve.catalogue.view.CatalogueView
import solve.menubar.view.MenuBarView
import solve.scene.view.SceneView
import tornadofx.*

class MainView : View() {
    override val root = borderpane {
        top<MenuBarView>()
        center<SceneView>()
        left<CatalogueView>()
    }
}