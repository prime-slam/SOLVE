package solve.catalogue.view.fields

import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import solve.catalogue.model.CatalogueField
import tornadofx.*

class CataloguePreviewImagesFieldsView : CatalogueFieldsView() {
    override val dragViewMaxFieldsNumber = 30
    override val listViewCellHeight = 110.0

    init {
        initialize()
    }

    private fun createPreviewImageView(field: CatalogueField): ImageView {
        val previewImage = field.loadPreviewImage(PreviewImageHeight)
        return imageview(previewImage) {
            fitHeight = PreviewImageHeight
            isPreserveRatio = true
        }
    }

    override fun setListViewCellFormat(label: Label, item: CatalogueField?) {
        super.setListViewCellFormat(label, item)
        if (item == null) {
            return
        }

        label.graphic = ImageView(item.loadPreviewImage(PreviewImageHeight))
        label.paddingLeft = 90.0
        label.contentDisplay = ContentDisplay.TOP
        label.text = item.fileName
    }

    override fun createFieldsSnapshotNode(fields: List<CatalogueField>) = vbox {
        fields.map {
            vbox {
                add(createPreviewImageView(it))
                label(it.fileName) {
                    alignment = Pos.CENTER
                }
                alignment = Pos.CENTER
            }
        }
    }

    override val root = fieldsListView

    companion object {
        private const val PreviewImageHeight = 80.0
    }
}
