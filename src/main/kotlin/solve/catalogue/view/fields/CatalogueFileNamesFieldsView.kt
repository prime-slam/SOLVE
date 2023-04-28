package solve.catalogue.view.fields

import javafx.scene.control.Labeled
import javafx.scene.image.ImageView
import solve.catalogue.model.CatalogueField
import solve.constants.IconsCatalogueImagePath
import solve.utils.loadResourcesImage
import tornadofx.*

class CatalogueFileNamesFieldsView : CatalogueFieldsView() {
    private val fileNamesFieldIconImage = loadResourcesImage(IconsCatalogueImagePath)

    override val dragViewMaxFieldsNumber = 100
    override val listViewCellHeight = 25.0

    init {
        initialize()
    }

    private fun createFieldIconImageView(): ImageView? {
        if (fileNamesFieldIconImage != null) {
            return imageview(fileNamesFieldIconImage) {
                fitHeight = ListViewFieldIconSize
                isPreserveRatio = true
            }
        }

        return null
    }

    override fun setListViewCellFormat(labeled: Labeled, item: CatalogueField?) {
        super.setListViewCellFormat(labeled, item)
        val iconImageView = createFieldIconImageView()
        if (iconImageView != null) {
            labeled.graphic = iconImageView
        }
        labeled.text = item?.fileName
    }

    override fun createFieldsSnapshotNode(fields: List<CatalogueField>) = vbox {
        fields.map {
            hbox(4) {
                val iconImageView = createFieldIconImageView()
                if (iconImageView != null) {
                    add(iconImageView)
                }
                label(it.fileName)
            }
        }
    }

    override val root = fieldsListView

    companion object {
        private const val ListViewFieldIconSize = 20.0
    }
}
