package sliv.tool.view

import tornadofx.*

class MainView : View() {
    override val root = borderpane {
        top<MenuBarView>()
        center<SceneView>()
        left<CatalogueView>()
        right<SettingsView>()
    }
}
