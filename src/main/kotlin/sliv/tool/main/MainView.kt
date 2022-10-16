package sliv.tool.main

import sliv.tool.catalogue.view.CatalogueView
import sliv.tool.menubar.view.MenuBarView
import sliv.tool.settings.view.SettingsView
import sliv.tool.scene.view.SceneView
import tornadofx.*

class MainView : View() {
    override val root = borderpane {
        top<MenuBarView>()
        center<SceneView>()
        left<CatalogueView>()
        right<SettingsView>()
    }
}
