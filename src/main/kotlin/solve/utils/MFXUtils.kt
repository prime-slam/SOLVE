package solve.utils

import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXContextMenu
import io.github.palexdev.materialfx.controls.MFXContextMenuItem
import io.github.palexdev.materialfx.controls.MFXTextField
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.attachTo

fun EventTarget.mfxButton(text: String = "", graphic: Node? = null, op: MFXButton.() -> Unit = {}) = MFXButton(text).attachTo(this, op) {
    if (graphic != null) it.graphic = graphic
}

fun EventTarget.mfxTextField(text: String = "", graphic: Node? = null, op: MFXTextField.() -> Unit = {}) = MFXTextField(text).attachTo(this, op)

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
