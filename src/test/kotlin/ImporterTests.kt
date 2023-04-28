import javafx.scene.control.TreeItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import solve.importer.ProjectParser.createTreeWithFiles
import solve.importer.ProjectParser.partialParseDirectory
import solve.importer.model.FileInTree
import solve.importer.model.FileInfo
import solve.importer.model.FrameAfterPartialParsing
import solve.importer.model.ImageAfterPartialParsing
import solve.importer.model.OutputAfterPartialParsing
import solve.importer.model.ProjectAfterPartialParsing
import solve.project.model.LayerKind

class ImporterTests {
    companion object {
        const val pathTestProject = "testData/TestProject1"
        const val image1 = "123456789"
        const val image2 = "987654321"
        const val layer = "alg1_keypoint"
        const val pathImage1 = "testData/TestProject1/images/123456789.jpg"
        const val pathImage2 = "testData/TestProject1/images/987654321.jpg"
        const val pathOutput1 = "testData/TestProject1/alg1_keypoint/123456789.csv"
        const val pathOutput2 = "testData/TestProject1/alg1_keypoint/987654321.csv"
    }

    @Test
    fun treeCreateTest() {
        val initTree = TreeItem(FileInTree(FileInfo()))

        val project = partialParseDirectory(pathTestProject)
        val result = project?.let { createTreeWithFiles(it, initTree) }

        val image1 = TreeItem(FileInTree(FileInfo(image1)))
        val image2 = TreeItem(FileInTree(FileInfo(image2)))

        image1.children.add(TreeItem(FileInTree(FileInfo(layer))))
        image2.children.add(TreeItem(FileInTree(FileInfo(layer))))

        initTree.children.addAll(image1, image2)

        Assertions.assertEquals(initTree, result)
    }

    @Test
    fun partialParseDirectoryTest() {
        val result = partialParseDirectory(pathTestProject)

        val frame1 = FrameAfterPartialParsing(
            ImageAfterPartialParsing(
                image1,
                pathImage1,
                mutableListOf()
            ),
            listOf(
                OutputAfterPartialParsing(
                    image1,
                    pathOutput1,
                    layer,
                    LayerKind.Keypoint,
                    mutableListOf()
                )
            )
        )

        val frame2 = FrameAfterPartialParsing(
            ImageAfterPartialParsing(
                image2,
                pathImage2,
                mutableListOf()
            ),
            listOf(
                OutputAfterPartialParsing(
                    image2,
                    pathOutput2,
                    layer,
                    LayerKind.Keypoint,
                    mutableListOf()
                )
            )
        )

        val expected = ProjectAfterPartialParsing(pathTestProject, listOf(frame1, frame2))

        Assertions.assertEquals(expected, result)
    }
}
