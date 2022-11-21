package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.SelectionMode
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.catalogue.deselectAllItems
import sliv.tool.catalogue.model.CatalogueField
import sliv.tool.catalogue.model.ViewFormat
import sliv.tool.catalogue.selectAllItems
import sliv.tool.catalogue.selectedItems
import tornadofx.*

class CatalogueView : View() {
    companion object {
        private val initialViewFormat = ViewFormat.FILE_NAME
    }

    private val controller: CatalogueController by inject()

    private var fields = FXCollections.observableArrayList<CatalogueField>()

    private val viewFormatToggleGroup = ToggleGroup()
    private lateinit var fileNameViewRadioButton: RadioButton
    private lateinit var imagePreviewRadioButton: RadioButton
    private var currentViewFormat = initialViewFormat

    private val checkedAllProperty = booleanProperty(false)

    init {
        controller.model.frames.onChange {
            resetNodes()
            fields.clear()
            fields.addAll(getFields())
        }
    }

    private fun getFields() = controller.model.frames.map { CatalogueField(it) }.toObservable()

    private val fileNamesListView = listview(fields) {
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        cellFormat {
            text = it.fileName
        }
    }

    private val catalogueBorderpane = borderpane {
        top = hbox {
            prefWidth = 300.0
            padding = Insets(5.0, 5.0, 5.0, 5.0)
            spacing = 5.0
            checkbox("Select all", checkedAllProperty) {
                action {
                    if (isSelected) {
                        fileNamesListView.selectAllItems()
                        checkedAllProperty.value = true
                    }
                    else fileNamesListView.deselectAllItems()
                }
            }
            pane().hgrow = Priority.ALWAYS
            fileNameViewRadioButton = radiobutton("File view", viewFormatToggleGroup)
            imagePreviewRadioButton = radiobutton("Image preview", viewFormatToggleGroup)
        }
        bottom {
            hbox(alignment = Pos.CENTER) {
                button("Apply") {
                    action {
                        controller.createFramesSelection(fileNamesListView.selectedItems().map { it.frame })
                    }
                }
            }
        }
    }

    override val root = catalogueBorderpane.also { initializeNodes() }

    private fun onViewFormatToggleSwitched(switchedToToggle: Toggle) {
        val selectedViewFormat = when (switchedToToggle as RadioButton) {
            fileNameViewRadioButton -> ViewFormat.FILE_NAME
            imagePreviewRadioButton -> ViewFormat.IMAGE_PREVIEW
            else -> ViewFormat.FILE_NAME.also { println("Unexpected view format toggle!") }
        }
        updateViewFormatNode(selectedViewFormat)
    }

    private fun getViewFormatNode(withFormat: ViewFormat) = when (withFormat) {
        ViewFormat.FILE_NAME -> fileNamesListView
        ViewFormat.IMAGE_PREVIEW -> null // TODO("Add an image preview format")
    }
    private fun updateViewFormatNode(withFormat: ViewFormat) {
        catalogueBorderpane.center = getViewFormatNode(withFormat)
    }

    private fun initializeNodes() {
        initializeViewFormatToggles()
        initializeSelectionNodes()
    }

    private fun getViewFormatToggle(viewFormat: ViewFormat) = when (viewFormat) {
        ViewFormat.FILE_NAME -> fileNameViewRadioButton
        ViewFormat.IMAGE_PREVIEW -> imagePreviewRadioButton
    }

    private fun initializeViewFormatToggles() {
        viewFormatToggleGroup.selectedToggleProperty().onChange {
            it ?: return@onChange
            onViewFormatToggleSwitched(it)
        }
        viewFormatToggleGroup.selectToggle(getViewFormatToggle(currentViewFormat))
    }

    private fun areSelectedAll() =
        fileNamesListView.selectionModel.selectedItems.count() == controller.model.frames.count()

    private fun initializeSelectionNodes() {
        fileNamesListView.selectionModel.selectedItemProperty().onChange {
            if (checkedAllProperty.value && !areSelectedAll()) {
                checkedAllProperty.value = false
            }
        }
    }

    private fun resetNodes() {
        checkedAllProperty.value = false
        viewFormatToggleGroup.selectToggle(getViewFormatToggle(initialViewFormat))
    }
}
