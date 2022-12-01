package sliv.tool.catalogue.view

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import sliv.tool.catalogue.*
import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.catalogue.model.CatalogueField
import sliv.tool.catalogue.model.ViewFormat
import sliv.tool.main.MainView
import sliv.tool.project.model.ProjectFrame
import sliv.tool.scene.view.SceneView
import tornadofx.*
import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class CatalogueView : View() {
    enum class SelectionState {
        All,
        None,
        Part
    }

    companion object {
        private val initialViewFormat = ViewFormat.FileName
        private val initialSelectionState = SelectionState.All
        private const val DragViewMaxFieldsNumber = 100

        private const val CatalogueWidth = 300.0
        private const val ListViewFieldCellHeight = 30.0
        private const val ListViewFieldIconSize = 20.0
    }

    private val controller: CatalogueController by inject()
    private val sceneView: SceneView by inject()
    private val mainView: MainView by inject()

    private val fields = FXCollections.observableArrayList<CatalogueField>()

    private val viewFormatToggleGroup = ToggleGroup()
    private lateinit var fileNameViewRadioButton: RadioButton
    private lateinit var imagePreviewRadioButton: RadioButton
    private var viewFormat: ViewFormat by viewFormatRadioButtonDelegate()

    private val selectionCheckBoxBoolProperty = booleanProperty(false)
    private lateinit var selectionCheckBox: CheckBox
    private var checkBoxSelectionState: SelectionState by checkBoxSelectionStateDelegate()

    private var isDisplayingInfoLabel = false

    private var isDragging = false

    private val currentSelectionState: SelectionState
        get() = when {
            areSelectedAllFields -> SelectionState.All
            isSelectionEmpty -> SelectionState.None
            else -> SelectionState.Part
        }
    private val areSelectedAllFields: Boolean
        get() = fileNamesListView.selectedItemsCount == controller.model.frames.count()
    private val isSelectionEmpty: Boolean
        get() = fileNamesListView.selectedItems.isEmpty()
    private val selectedFrames: List<ProjectFrame>
        get() = fileNamesListView.selectedItems.map { it.frame }

    init {
        controller.model.frames.onChange {
            reinitializeFields()
            visualizeProjectImportSelection()
            resetNodes()
        }
    }
    private fun onSceneDragDropped(event: DragEvent) {
        if (isDragging)
            controller.visualizeFramesSelection(selectedFrames)

        isDragging = false
    }

    private fun onSceneDragOver(event: DragEvent) {
        if (isDragging)
            event.acceptTransferModes(TransferMode.MOVE)
    }

    private fun onCatalogueDragDetected() {
        val dragboard = mainView.root.startDragAndDrop(TransferMode.MOVE)
        val clipboardContent = ClipboardContent()
        clipboardContent.putString("") // It is necessary to display a drag view image.
        dragboard.setContent(clipboardContent)
        dragboard.dragView = createFileNameFieldsSnapshot(fileNamesListView.selectedItems)
        isDragging = true
    }

    private fun reinitializeFields() {
        val newFields = controller.model.frames.map { CatalogueField(it) }.toObservable()
        fields.clear()
        fields.addAll(newFields)
    }

    private val infoLabel = label()

    private fun setFileNamesListViewCellFormat(labeled: Labeled, item: CatalogueField?) {
        labeled.text = item?.fileName
        labeled.graphic = imageview(fileNamesFieldIconImage) {
            fitHeight = ListViewFieldIconSize
            isPreserveRatio = true
        }
        labeled.prefHeight = ListViewFieldCellHeight
    }

    private val fileNamesFieldIconImage =
        Image(this.javaClass.classLoader.getResource("catalogue_image_icon.png")?.openStream())

    private val fileNamesListView = listview(fields) {
        selectionModel.selectionMode = SelectionMode.MULTIPLE

        cellFormat {
            setFileNamesListViewCellFormat(this, it)
        }
    }

    private val catalogueBorderpane = borderpane {
        prefWidth = CatalogueWidth
        top = vbox {
            hbox {
                padding = Insets(5.0, 5.0, 5.0, 5.0)
                spacing = 5.0
                selectionCheckBox = checkbox("Select all", selectionCheckBoxBoolProperty) {
                    action {
                        if (isSelected) {
                            fileNamesListView.selectAllItems()
                        } else {
                            fileNamesListView.deselectAllItems()
                        }
                    }
                }
                pane().hgrow = Priority.ALWAYS
                fileNameViewRadioButton = radiobutton("File view", viewFormatToggleGroup)
                imagePreviewRadioButton = radiobutton("Image preview", viewFormatToggleGroup)
            }
            add(infoLabel)
        }
        bottom {
            hbox(alignment = Pos.CENTER) {
                button("Apply") {
                    action {
                        controller.visualizeFramesSelection(fileNamesListView.selectedItems.map { it.frame })
                    }
                }
            }
        }
    }

    override val root = catalogueBorderpane.also { initializeNodes() }

    private fun createFileNameFieldsSnapshot(fields: List<CatalogueField>): Image {
        val snapshotFields = fields.take(DragViewMaxFieldsNumber).asObservable()
        val prefSnapshotHeight = (snapshotFields.count() * ListViewFieldCellHeight).floor()

        val fieldsSnapshotNode = listview(snapshotFields) {
            cellFormat {
                setFileNamesListViewCellFormat(this, it)
            }
        }
        val snapshotScene = Scene(fieldsSnapshotNode)
        val nodeSnapshot = fieldsSnapshotNode.snapshot(null, null)
        return WritableImage(
            nodeSnapshot.pixelReader, nodeSnapshot.width.floor(), min(nodeSnapshot.height.floor(), prefSnapshotHeight)
        )
    }

    private fun displayInfoLabel(withText: String) {
        infoLabel.text = withText
        infoLabel.isVisible = true
        infoLabel.isManaged = true
        isDisplayingInfoLabel = true
    }

    private fun hideInfoLabel() {
        if (!isDisplayingInfoLabel) {
            return
        }

        infoLabel.isManaged = false
        infoLabel.isVisible = false
        isDisplayingInfoLabel = false
    }

    private fun checkForEmptyFields() {
        if (fields.isEmpty()) {
            displayInfoLabel("No files found!")
        } else {
            hideInfoLabel()
        }
    }

    private fun getViewFormatNode(withFormat: ViewFormat) = when (withFormat) {
        ViewFormat.FileName -> fileNamesListView
        ViewFormat.ImagePreview -> null // TODO("Add an image preview format")
    }

    private fun updateViewFormatNode(withFormat: ViewFormat) {
        catalogueBorderpane.center = getViewFormatNode(withFormat)
    }

    private fun initializeNodes() {
        initializeViewFormatRadioButtons()
        initializeSelectionNodes()
        initializeDragEvents()
        resetNodes()
    }

    private fun getRadioButtonViewFormat(radioButton: RadioButton) = when (radioButton) {
        fileNameViewRadioButton -> ViewFormat.FileName
        imagePreviewRadioButton -> ViewFormat.ImagePreview
        else -> ViewFormat.FileName.also { println("Unexpected view format radio button!") }
    }

    private fun viewFormatRadioButtonDelegate(): ReadWriteProperty<Any?, ViewFormat> =
        object : ReadWriteProperty<Any?, ViewFormat> {
            var currentValue = initialViewFormat
            override fun getValue(thisRef: Any?, property: KProperty<*>): ViewFormat = currentValue
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: ViewFormat) {
                currentValue = value
                updateViewFormatNode(currentValue)
                viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(currentValue))
            }
        }

    private fun getViewFormatRadioButton(viewFormat: ViewFormat) = when (viewFormat) {
        ViewFormat.FileName -> fileNameViewRadioButton
        ViewFormat.ImagePreview -> imagePreviewRadioButton
    }

    private fun initializeViewFormatRadioButtons() {
        viewFormatToggleGroup.selectedToggleProperty().onChange {
            it ?: return@onChange
            viewFormat = getRadioButtonViewFormat(it as RadioButton)
        }
    }

    private fun checkBoxSelectionStateDelegate(): ReadWriteProperty<Any?, SelectionState> =
        object : ReadWriteProperty<Any?, SelectionState> {
            var currentValue = initialSelectionState
            override fun getValue(thisRef: Any?, property: KProperty<*>): SelectionState = currentValue
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: SelectionState) {
                currentValue = value
                when (currentValue) {
                    SelectionState.All -> {
                        selectionCheckBox.isIndeterminate = false
                        selectionCheckBoxBoolProperty.value = true
                    }
                    SelectionState.None -> {
                        selectionCheckBox.isIndeterminate = false
                        selectionCheckBoxBoolProperty.value = false
                    }
                    SelectionState.Part -> {
                        selectionCheckBox.isIndeterminate = true
                        selectionCheckBoxBoolProperty.value = false
                    }
                }
            }
        }

    private fun initializeSelectionNodes() {
        fileNamesListView.onSelectionChanged {
            checkBoxSelectionState = currentSelectionState
        }
        fileNamesListView.setOnMouseClicked {
            checkBoxSelectionState = currentSelectionState
        }
    }

    private fun initializeDragEvents() {
        fileNamesListView.setOnDragDetected {
            onCatalogueDragDetected()
        }
        sceneView.root.addEventFilter(DragEvent.DRAG_OVER, ::onSceneDragOver)
        sceneView.root.addEventFilter(DragEvent.DRAG_DROPPED, ::onSceneDragDropped)
    }

    private fun visualizeProjectImportSelection() {
        fileNamesListView.selectAllItems()
        controller.visualizeFramesSelection(selectedFrames)
    }

    private fun resetNodes() {
        checkBoxSelectionState = currentSelectionState
        viewFormatToggleGroup.selectToggle(getViewFormatRadioButton(initialViewFormat))
        checkForEmptyFields()
    }
}
