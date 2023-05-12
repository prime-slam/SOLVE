package solve.utils.materialfx

import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXCheckListView
import io.github.palexdev.materialfx.controls.MFXCheckbox
import io.github.palexdev.materialfx.controls.MFXContextMenu
import io.github.palexdev.materialfx.controls.MFXContextMenuItem
import io.github.palexdev.materialfx.controls.MFXTextField
import io.github.palexdev.materialfx.effects.ripple.RippleClipType
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import org.controlsfx.control.RangeSlider
import solve.utils.materialfx.stylesheets.MFXRangeSliderStylesheet
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

fun Node.mfxCircleButton(
    graphic: Node? = null,
    radius: Double = 20.0,
    text: String = "",
    op: MFXButton.() -> Unit = {}
) = mfxButton(text, graphic) {
    this.graphic = graphic
    rippleGenerator.clipSupplier = Supplier {
        RippleClipTypeFactory(RippleClipType.CIRCLE).setRadius(radius).build(this)
    }
    style {
        backgroundColor += Color.TRANSPARENT
        minHeight = Dimension(radius, Dimension.LinearUnits.px)
    }
    op()
}

fun EventTarget.mfxCheckbox(text: String? = null, op: MFXCheckbox.() -> Unit = {}): MFXCheckbox {
    val mfxCheckbox = MFXCheckbox(text)
    mfxCheckbox.attachTo(this, op)

    return mfxCheckbox
}

fun <T> EventTarget.mfxCheckListView(
    items: ObservableList<T> = observableListOf(),
    op: MFXCheckListView<T>.() -> Unit = {}
): MFXCheckListView<T> {
    val mfxCheckListView = MFXCheckListView(items)
    mfxCheckListView.attachTo(this, op)

    return mfxCheckListView
}

fun EventTarget.mfxRangeSlider(
    min: Double,
    max: Double,
    lowValue: Double,
    highValue: Double,
    op: RangeSlider.() -> Unit = {}
): RangeSlider {
    val slider = RangeSlider(min, max, lowValue, highValue)
    slider.addStylesheet(MFXRangeSliderStylesheet::class)
    slider.attachTo(this, op)

    return slider
}

val MFXTextField.validationMessage: String?
    get() {
        val constraints = validator.validate()
        if (constraints.isEmpty()) {
            return null
        }

        return constraints.first().message
    }
