package solve.filters.settings.view.controls

import solve.filters.settings.model.IndicesStepFilterSetting
import solve.utils.materialfx.MFXIntegerTextField

class IndicesStepSettingControl(
    controlNode: MFXIntegerTextField
) : FilterSettingControl<MFXIntegerTextField, IndicesStepFilterSetting>(controlNode) {
    override fun extrudeFilterSettings(): IndicesStepFilterSetting? {
        if (!controlNode.isValid || controlNode.text.isEmpty()) {
            return null
        }

        val stepNumber = controlNode.text.toInt()

        return IndicesStepFilterSetting(stepNumber)
    }
}
