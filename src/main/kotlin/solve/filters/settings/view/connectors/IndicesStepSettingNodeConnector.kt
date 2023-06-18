package solve.filters.settings.view.connectors

import solve.filters.settings.model.IndicesStepFilterSetting
import solve.utils.materialfx.MFXIntegerTextField

object IndicesStepSettingNodeConnector : FilterSettingNodeConnector<MFXIntegerTextField, IndicesStepFilterSetting>(
    MFXIntegerTextField::class,
    IndicesStepFilterSetting::class
) {
    override fun extractFilterSettingsFromTypedSettingNode(
        settingNode: MFXIntegerTextField
    ): IndicesStepFilterSetting? {
        if (!settingNode.isValid || settingNode.text.isEmpty()) {
            return null
        }

        val stepNumber = settingNode.text.toInt()

        return IndicesStepFilterSetting(stepNumber)
    }

    override fun updateTypedSettingNodeWithSettings(
        settingNode: MFXIntegerTextField,
        setting: IndicesStepFilterSetting
    ) {
        settingNode.text = setting.settingValue.toString()
    }

    override fun setDefaultTypedSettingNodeState(settingNode: MFXIntegerTextField) {
        settingNode.text = ""
    }
}
