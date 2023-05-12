package solve.filters.view

import javafx.scene.paint.Paint
import solve.constants.IconsFiltersAddPath
import solve.filters.settings.view.FilterSettingsView
import solve.project.controller.ProjectController
import solve.styles.SeparatorStylesheet
import solve.styles.Style
import solve.utils.createHGrowHBox
import solve.utils.createPxValue
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
import solve.utils.materialfx.mfxCircleButton
import tornadofx.*

class FilterPanelView : View() {
    private val filterPanelFieldsView: FilterPanelFieldsView by inject()
    private val filterSettingsView: FilterSettingsView by inject()
    private val projectController: ProjectController by inject()

    override val root = vbox {
        separator {
            addStylesheet(SeparatorStylesheet::class)
        }
        hbox {
            minHeight = HeaderMinHeight

            label("Filters") {
                style {
                    fontFamily = Style.Font
                    fontSize = createPxValue(HeaderFontSize)
                    textFill = Paint.valueOf(Style.HeaderFontColor)
                }

                paddingLeft = 15.0
            }
            add(createHGrowHBox())
            val addButtonIconImage = loadResourcesImage(IconsFiltersAddPath)
            val addButtonGraphic = if (addButtonIconImage != null) {
                imageViewIcon(addButtonIconImage, FilterFieldButtonsSize)
            } else {
                null
            }
            hbox {
                mfxCircleButton(addButtonGraphic, AddFilterButtonSize) {
                    enableWhen(projectController.model.projectProperty.booleanBinding { it != null })
                    action {
                        filterSettingsView.showCreationDialog()
                    }
                }
                paddingRight = 14.0
                paddingTop = -6.0
            }

            paddingTop = 10.0
        }
        add(filterPanelFieldsView)
    }

    companion object {
        private const val HeaderMinHeight = 48.0
        private const val HeaderFontSize = 14.0

        private const val FilterFieldButtonsSize = 24.0
        private const val AddFilterButtonSize = 15.0
    }
}
