package solve.filters.settings.view.connectors

import solve.filters.settings.model.UIDFilterSetting
import solve.utils.materialfx.MFXIntegerTextField

object UIDSettingNodeConnector : FilterSettingNodeConnector<MFXIntegerTextField, UIDFilterSetting>(
    MFXIntegerTextField::class,
    UIDFilterSetting::class
) {
    override fun extractFilterSettingsFromTypedSettingNode(settingNode: MFXIntegerTextField): UIDFilterSetting? {
        if (!settingNode.isValid || settingNode.text.isEmpty()) {
            return null
        }

        val uid = settingNode.text.toLong()

        return UIDFilterSetting(uid)
    }

    override fun updateTypedSettingNodeWithSettings(settingNode: MFXIntegerTextField, setting: UIDFilterSetting) {
        settingNode.text = setting.settingValue.toString()
    }

    override fun setDefaultTypedSettingNodeState(settingNode: MFXIntegerTextField) {
        settingNode.text = ""
    }
}
