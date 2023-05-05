package solve.utils

import de.codecentric.centerdevice.javafxsvg.dimension.Dimension
import de.codecentric.centerdevice.javafxsvg.dimension.DimensionProvider
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider
import org.w3c.dom.Document

class SVGImageLoaderDimensionProvider : DimensionProvider {
    private val primitiveDimensionProvider = PrimitiveDimensionProvider()

    override fun getDimension(document: Document?): Dimension {
        val primitiveDimension = primitiveDimensionProvider.getDimension(document)

        return Dimension(
            primitiveDimension.width * PrimitiveDimensionScale,
            primitiveDimension.height * PrimitiveDimensionScale
        )
    }

    companion object {
        private const val PrimitiveDimensionScale = 1.5f // Scale value for the primitive calculated dimension.
    }
}
