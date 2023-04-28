package solve.importer.model

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class CustomButton {
    private val disabledProperty = SimpleBooleanProperty()
    var disabled by disabledProperty
}

class ButtonModel : ItemViewModel<CustomButton>() {
    val disabled = bind(CustomButton::disabled)
}
