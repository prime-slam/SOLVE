package solve.catalogue.view.fields

import javafx.geometry.Pos
import javafx.scene.control.Labeled
import solve.catalogue.model.CatalogueField
import javafx.scene.image.ImageView
import tornadofx.*

class CataloguePreviewImagesFieldsView : CatalogueFieldsView() {
    companion object {
        private const val PreviewImageHeight = 80.0
    }

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

    override fun setListViewCellFormat(labeled: Labeled, item: CatalogueField?) {
        super.setListViewCellFormat(labeled, item)
        if (item == null) {
            return
        }

        labeled.graphic = vbox {
            alignment = Pos.CENTER
            add(createPreviewImageView(item))
            text(item.fileName)
        }
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
}
