package solve.parsers

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


internal enum class ImageFormat {
    JPG,
    PNG
}

internal fun createFileWithImageData(image: BufferedImage, format: ImageFormat, tempFolder: File): File {
    val imageFile = File(tempFolder, "data.${format.name}")
    ImageIO.write(image, format.name, imageFile)

    return imageFile
}
