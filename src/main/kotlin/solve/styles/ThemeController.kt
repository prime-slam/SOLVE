package solve.styles

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import kotlin.reflect.KClass

class ThemeController: Controller() {
    val themes = SimpleListProperty(listOf(LightTheme::class, DarkTheme::class).asObservable())

    val activeThemeProperty = SimpleObjectProperty<KClass<out Stylesheet>>()
    var activeTheme: KClass<out Stylesheet> by activeThemeProperty

    fun start() {
        activeThemeProperty.addListener { _, oldTheme, newTheme ->
            oldTheme?.let { removeStylesheet(it) }
            newTheme?.let { importStylesheet(it) }
        }

        activeTheme = themes.first()
    }
}