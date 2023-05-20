package solve.catalogue.view.fields

import javafx.scene.control.Labeled
import javafx.scene.image.ImageView
import javafx.scene.text.Font
import solve.catalogue.model.CatalogueField
import solve.constants.IconsCatalogueImagePath
import solve.styles.Style
import solve.utils.loadResourcesImage
import javafx.scene.control.Label
import tornadofx.*

class CatalogueFileNamesFieldsView : CatalogueFieldsView() {
    override val dragViewMaxFieldsNumber = 100
    override val listViewCellHeight = 35.0

    init {
        initialize()
    }

    override fun setListViewCellFormat(label: Label, item: CatalogueField?) {
        super.setListViewCellFormat(label, item)
        if (item != null) {
            label.text = item.fileName
        }
    }

    override fun createFieldsSnapshotNode(fields: List<CatalogueField>) = vbox {
        fields.map {
            hbox(4) {
                label(it.fileName)
            }
        }
    }

    override val root = fieldsListView
}
