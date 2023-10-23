package solve.utils

import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

// Loads image from the resources folder.
fun loadResourcesImage(resourcesPath: String): Image? {
    val imageFileStream = getResource(resourcesPath)?.openStream() ?: return null

    return Image(imageFileStream)
}

fun readResourcesFileText(resourcesPath: String): String? {
    val fileAbsolutePath = getResourceAbsolutePath(resourcesPath) ?: return null
    return readFileText(fileAbsolutePath)
}

// Reads a text of the file at the given path.
fun readFileText(filePath: String): String? {
    val file = File(filePath)

    if (!file.canRead()) {
        println("The text file cannot be read!")
        return null
    }

    var text: String? = null
    try {
        text = file.readText()
    } catch (exception: IOException) {
        println("Input error while reading the text!\n${exception.message}")
    }

    return text
}

// Loads a buffered image at the given path.
fun loadBufferedImage(filePath: String): BufferedImage? {
    val file = File(filePath)

    if (!file.canRead()) {
        println("The image file cannot be read!")
        return null
    }

    var image: BufferedImage? = null
    try {
        image = ImageIO.read(file)
    } catch (exception: IOException) {
        println("Input error while loading the image!\n${exception.message}")
    }

    return image
}

fun getResourceAbsolutePath(resourcesPath: String): String? {
    val resourceAbsolutePath = getResource(resourcesPath)?.path
    if (resourceAbsolutePath.isNullOrEmpty())
        return null

    return resourceAbsolutePath.substring(1 .. resourceAbsolutePath.lastIndex)
}

private fun getResource(resourcesPath: String) = Any::class::class.java.getResource("/$resourcesPath")
