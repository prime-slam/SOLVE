package solve.catalogue.view.fields

import javafx.scene.control.Labeled

import solve.utils.loadImage
import solve.catalogue.model.CatalogueField
import javafx.scene.image.ImageView
import tornadofx.*

class CatalogueFileNamesFieldsView : CatalogueFieldsView() {
    companion object {
        private const val ListViewFieldIconSize = 20.0
    }

    private val fileNamesFieldIconImage = loadImage("icons/catalogue/catalogue_image_icon.png")
    private val fileNamesFieldIconImageDark = loadImage("icons/catalogue/catalogue_image_icon_dark_theme.png")

    override val dragViewMaxFieldsNumber = 100
    override val listViewCellHeight = 25.0

    init {
        initialize()
    }

    private fun createFieldIconImageView(): ImageView? {
        if (fileNamesFieldIconImageDark != null) {
           return imageview(fileNamesFieldIconImageDark) {
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
}