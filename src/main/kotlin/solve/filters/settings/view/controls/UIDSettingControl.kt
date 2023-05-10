package solve.filters.settings.view.controls

import solve.filters.settings.model.UIDFilterSetting
import solve.utils.materialfx.MFXIntegerTextField

class UIDSettingControl(
    controlNode: MFXIntegerTextField
) : FilterSettingControl<MFXIntegerTextField, UIDFilterSetting>(controlNode) {
    override fun extrudeFilterSettings(): UIDFilterSetting? {
        if (!controlNode.isValid || controlNode.text.isEmpty()) {
            return null
        }

        val uid = controlNode.text.toLong()

        return UIDFilterSetting(uid)
    }
}
