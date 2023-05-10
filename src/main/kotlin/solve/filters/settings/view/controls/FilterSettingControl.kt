package solve.filters.settings.view.controls

import javafx.scene.Node
import solve.filters.settings.model.FilterSetting

abstract class FilterSettingControl<T : Node, K : FilterSetting<*>>(protected val controlNode: T) {
    abstract fun extrudeFilterSettings(): FilterSetting<*>?
}
