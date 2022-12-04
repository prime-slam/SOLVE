package solve.catalogue.view.fields

import javafx.geometry.Pos
import javafx.scene.control.Labeled
import solve.catalogue.model.CatalogueField
import solve.catalogue.view.fields.CatalogueFieldsView
import tornadofx.*

class CataloguePreviewImagesFieldsView : CatalogueFieldsView() {
    companion object {
        private const val previewImageHeight = 80.0
    }

    override val dragViewMaxFieldsNumber = 20
    override val listViewCellHeight = 100.0

    init {
        initialize()
    }

    override fun setListViewCellFormat(labeled: Labeled, item: CatalogueField?) {
        super.setListViewCellFormat(labeled, item)
        val previewImage = item?.loadPreviewImage(previewImageHeight)
        labeled.graphic = vbox {
            alignment = Pos.CENTER
            if (previewImage != null) {
                imageview(previewImage) {
                    fitHeight = previewImageHeight
                    isPreserveRatio = true
                }
            }
            label(item?.fileName ?: "")
        }
    }

    override val root = fieldsListView
}