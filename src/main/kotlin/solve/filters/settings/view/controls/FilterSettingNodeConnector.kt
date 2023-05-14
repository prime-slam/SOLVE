package solve.filters.settings.view.controls

import javafx.scene.Node
import solve.filters.settings.model.FilterSetting
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class FilterSettingNodeConnector<T : Node, K : FilterSetting<out Any>> (
    private val nodeKClass: KClass<T>,
    val filterSettingKCLass: KClass<K>
) {
    fun extractFilterSettings(settingNode: Node): K? {
        if (!isExpectedNodeType(settingNode)) {
            return null
        }

        return extractFilterSettingsFromTypedSettingNode(castNodeToExpectedType(settingNode))
    }

    fun updateSettingNodeWithSettings(settingNode: Node, setting: FilterSetting<out Any>) {
        if (!isExpectedNodeType(settingNode) || !filterSettingKCLass.isInstance(setting)) {
            return
        }

        return updateTypedSettingNodeWithSettings(
            castNodeToExpectedType(settingNode),
            filterSettingKCLass.cast(setting)
        )
    }

    fun setDefaultSettingNodeState(settingNode: Node) {
        if (!isExpectedNodeType(settingNode)) {
            return
        }

        return setDefaultTypedSettingNodeState(castNodeToExpectedType(settingNode))
    }

    protected abstract fun extractFilterSettingsFromTypedSettingNode(settingNode: T): K?

    protected abstract fun updateTypedSettingNodeWithSettings(settingNode: T, setting: K)

    protected abstract fun setDefaultTypedSettingNodeState(settingNode: T)

    private fun isExpectedNodeType(settingNode: Node) = nodeKClass.isInstance(settingNode)

    private fun castNodeToExpectedType(settingNode: Node) = nodeKClass.cast(settingNode)
}
