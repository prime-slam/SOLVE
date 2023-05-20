package solve.utils

import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXCheckListView
import io.github.palexdev.materialfx.controls.MFXContextMenu
import io.github.palexdev.materialfx.controls.MFXContextMenuItem
import io.github.palexdev.materialfx.controls.MFXTextField
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.*
import java.util.function.Supplier

fun EventTarget.mfxButton(
    text: String = "",
    graphic: Node? = null,
    op: MFXButton.() -> Unit = {}
) = MFXButton(text).attachTo(this, op) {
    if (graphic != null) it.graphic = graphic
}

fun EventTarget.mfxTextField(
    text: String = "",
    op: MFXTextField.() -> Unit = {}
) = MFXTextField(text).attachTo(this, op)

fun Node.mfxContextMenu(op: MFXContextMenu.() -> Unit = {}): MFXContextMenu {
    val contextMenu = MFXContextMenu.Builder.build(this).installAndGet()
    op(contextMenu)
    return contextMenu
}

fun MFXContextMenu.item(
    name: String,
    op: MFXContextMenuItem.() -> Unit = {}
) = MFXContextMenuItem(name).also {
    op(it)
    this.addItems(it)
}

fun MFXContextMenu.lineSeparator() = this.addLineSeparator(MFXContextMenu.Builder.getLineSeparator())

fun MFXContextMenuItem.action(op: () -> Unit) = this.setOnAction { op() }

fun EventTarget.mfxCircleButton(
    graphic: Node? = null,
    radius: Double = 20.0,
    text: String = "",
    op: MFXButton.() -> Unit = {}
) = mfxButton(text, graphic) {
    this.graphic = graphic
    Platform.runLater {
        val rippleCenter = boundsInLocal
        rippleGenerator.clipSupplier = Supplier {
            return@Supplier Circle(rippleCenter.centerX, rippleCenter.centerY, radius)
        }
    }
    style {
        backgroundColor += Color.TRANSPARENT
        minHeight = Dimension(radius, Dimension.LinearUnits.px)
    }
    op()
}

fun <T> EventTarget.mfxCheckListView(
    items: ObservableList<T> = observableListOf(),
    op: MFXCheckListView<T>.() -> Unit = {}
): MFXCheckListView<T> {
    val mfxCheckListView = MFXCheckListView(items)
    mfxCheckListView.attachTo(this, op)

    return mfxCheckListView
}

