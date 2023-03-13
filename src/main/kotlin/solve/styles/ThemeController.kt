package solve.styles

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import kotlin.reflect.KClass

class ThemeController: Controller() {
    val themes = SimpleListProperty(listOf(LightTheme::class, DarkTheme::class).asObservable())

    // Property holding the active theme
    val activeThemeProperty = SimpleObjectProperty<KClass<out Stylesheet>>()
    var activeTheme: KClass<out Stylesheet> by activeThemeProperty

    fun start() {
        // Remove old theme, add new theme on change
        activeThemeProperty.addListener { _, oldTheme, newTheme ->
            oldTheme?.let { removeStylesheet(it) }
            newTheme?.let { importStylesheet(it) }
        }

        // Activate the first theme, triggering the listener above
        activeTheme = themes.first()

    }
}