package solve.menubar.view

import solve.styles.DarkTheme
import solve.importer.controller.ImporterController
import solve.importer.view.ImporterView
import solve.styles.LightTheme
import solve.styles.ThemeController
import tornadofx.*

class MenuBarView : View() {
    val importer = find<ImporterView>()
    val controller: ImporterController by inject()
    val settings: ThemeController by inject()

    override val root = borderpane {
        addClass(DarkTheme.backgroundElement)
        left {
            hbox {
                addClass(DarkTheme.backgroundElement)
                button("Import project") {
                    action {
                        controller.directoryPath.set(null)
                        controller.projectAfterPartialParsing.set(null)
                        importer.openModal()
                    }
                }
                button("Manage plugins")
                button("Settings")
                button("Help")
                this.children.forEach { it.addClass(LightTheme.menuBarButton) }
            }
        }
        right {
            form {
                paddingBottom = 0.0
                fieldset {
                    paddingBottom = 0.0
                    field {

                        hbox {
                            togglegroup {
                                paddingRight = 10.0

                                settings.themes

                                settings.themes.forEach { theme ->
                                    radiobutton(
                                        theme.simpleName?.split("T")?.first(),
                                        getToggleGroup(),
                                        theme
                                    ).apply { paddingRight = 5.0 }
                                }

                                bind(settings.activeThemeProperty)
                            }
                        }
                    }
                }
            }
        }
    }
}