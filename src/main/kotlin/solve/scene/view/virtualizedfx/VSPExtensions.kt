package solve.scene.view.virtualizedfx

import io.github.palexdev.mfxcore.base.beans.Position
import io.github.palexdev.mfxcore.base.bindings.*
import io.github.palexdev.mfxcore.builders.bindings.ObjectBindingBuilder
import io.github.palexdev.virtualizedfx.beans.VirtualBounds
import io.github.palexdev.virtualizedfx.cell.GridCell
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane
import io.github.palexdev.virtualizedfx.controls.skins.VirtualScrollPaneSkin
import io.github.palexdev.virtualizedfx.grid.VirtualGrid
import javafx.geometry.Orientation
import javafx.scene.control.Skin
import javafx.scene.layout.Pane
import solve.scene.view.association.OutOfFramesLayer
import tornadofx.add

// This function is identical to VSPUtils.wrap(VirtualGrid) but creates inheritor for VSP
fun <T, C : GridCell<T>?> wrapGridWithVsp(
    grid: VirtualGrid<T, C>, outOfFramesLayer: OutOfFramesLayer
): VirtualScrollPane {
    val vsp = VirtualScrollPaneWithOutOfFramesLayer(grid, outOfFramesLayer)
    val bindings = MFXBindings.instance()
    vsp.orientation = Orientation.VERTICAL
    vsp.contentBoundsProperty().bind(ObjectBindingBuilder.build<VirtualBounds>().setMapper {
        val eSize = grid.estimatedSize
        VirtualBounds.of(grid.width, grid.height, eSize.width, eSize.height)
    }.addSources(grid.widthProperty(), grid.heightProperty(), grid.estimatedSizeProperty()).get())
    val vSource =
        MappingSource.of<Position, Number>(grid.positionProperty()).setTargetUpdater(Mapper.of { `val`: Position ->
            val helper = grid.gridHelper
            val max = helper.maxVScroll()
            if (max != 0.0) `val`.y / max else 0
        }) { o: Number?, n: Number -> vsp.vVal = n.toDouble() }.setSourceUpdater(Mapper.of { `val`: Number ->
            val helper = grid.gridHelper
            val currPos = grid.position
            Position.of(currPos.x, `val`.toDouble() * helper.maxVScroll())
        }) { o: Position?, n: Position? ->
            grid.setPosition(
                n
            )
        }
    bindings.bindBidirectional(vsp.vValProperty()).addSources(vSource)
        .addTargetInvalidatingSource(grid.heightProperty()).get()
    val hSource =
        MappingSource.of<Position, Number>(grid.positionProperty()).setTargetUpdater(Mapper.of { `val`: Position ->
            val helper = grid.gridHelper
            val max = helper.maxHScroll()
            if (max != 0.0) `val`.x / max else 0
        }) { o: Number?, n: Number -> vsp.hVal = n.toDouble() }.setSourceUpdater(Mapper.of { `val`: Number ->
            val helper = grid.gridHelper
            val currPos = grid.position
            Position.of(`val`.toDouble() * helper.maxHScroll(), currPos.y)
        }) { o: Position?, n: Position? ->
            grid.setPosition(
                n
            )
        }
    bindings.bindBidirectional(vsp.hValProperty()).addSources(hSource)
        .addTargetInvalidatingSource(grid.widthProperty()).get()
    return vsp
}

// This inheritor is needed to put OutOfFramesLayer into visual tree nearby VirtualGrid
private class VirtualScrollPaneWithOutOfFramesLayer<T, C : GridCell<T>?>(
    private val virtualGrid: VirtualGrid<T, C>, private val outOfFramesLayer: OutOfFramesLayer
) : VirtualScrollPane(virtualGrid) {
    override fun createDefaultSkin(): Skin<*> {
        return VirtualScrollPaneWithOutOfFramesLayerSkin(this, virtualGrid, outOfFramesLayer)
    }
}

// This inheritor is needed to put OutOfFramesLayer into visual tree nearby VirtualGrid
private class VirtualScrollPaneWithOutOfFramesLayerSkin<T, C : GridCell<T>?>(
    vsp: VirtualScrollPane,
    virtualGrid: VirtualGrid<T, C>,
    outOfFramesLayer: OutOfFramesLayer,
) : VirtualScrollPaneSkin(vsp) {
    init {
        outOfFramesLayer.layoutXProperty().bind(ObjectBindingBuilder.build<Number>().setMapper {
            -vsp.hVal * virtualGrid.gridHelper.maxHScroll() // This approach is used by original VSP for VirtualGrid
        }.addSources(vsp.hValProperty()).get())
        outOfFramesLayer.layoutYProperty().bind(ObjectBindingBuilder.build<Number>().setMapper {
            -vsp.vVal * virtualGrid.gridHelper.maxVScroll()
        }.addSources(vsp.vValProperty()).get())

        val container = children.first { x -> x is Pane }
        container.add(outOfFramesLayer)
    }
}