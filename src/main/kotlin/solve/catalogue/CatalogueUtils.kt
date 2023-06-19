package solve.catalogue

import io.github.palexdev.materialfx.controls.MFXCheckListView
import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer

fun <T> MFXCheckListView<T>.selectionModelProperty(): ObjectProperty<IMultipleSelectionModel<T>> {
    return SimpleObjectProperty(selectionModel)
}

fun <T> MFXCheckListView<T>.setSelectionModel(value: IMultipleSelectionModel<T>) {
    selectionModelProperty().set(value)
}

val ProjectFrame.layers: List<ProjectLayer>
    get() = landmarkFiles.map { it.projectLayer }.distinct()

fun <T> synchronizeListViewsSelections(firstListView: MFXCheckListView<T>, secondListView: MFXCheckListView<T>) {
    secondListView.setSelectionModel(firstListView.selectionModel)
}
