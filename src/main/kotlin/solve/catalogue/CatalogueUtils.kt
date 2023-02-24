package solve.catalogue

import javafx.scene.control.Labeled
import javafx.scene.control.ListView
import solve.project.model.ProjectFrame
import solve.project.model.ProjectLayer
import tornadofx.tooltip

import kotlin.math.ceil

val ProjectFrame.layers: List<ProjectLayer>
    get() = landmarkFiles.map { it.projectLayer }.distinct()

fun Double.ceil(): Int = ceil(this).toInt()

fun Double.floor(): Int = kotlin.math.floor(this).toInt()

fun <T> synchronizeListViewsSelections(firstListView: ListView<T>, secondListView: ListView<T>) {
    secondListView.selectionModel = firstListView.selectionModel
}

fun Labeled.addNameTooltip() {
    tooltip(text)
}
