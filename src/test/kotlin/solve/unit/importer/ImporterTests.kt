package solve.unit.importer

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
    @Test
    fun treeCreateTest() {
        val initTree = TreeItem(FileInTree(FileInfo()))

        val project = partialParseDirectory(PathTestProject)
        val result = project?.let { createTreeWithFiles(it, initTree) }

        val image1 = TreeItem(FileInTree(FileInfo(Image1)))
        val image2 = TreeItem(FileInTree(FileInfo(Image2)))

        image1.children.add(TreeItem(FileInTree(FileInfo(Layer))))
        image2.children.add(TreeItem(FileInTree(FileInfo(Layer))))

        initTree.children.addAll(image1, image2)

        Assertions.assertEquals(initTree, result)
    }

    @Test
    fun partialParseDirectoryTest() {
        val result = partialParseDirectory(PathTestProject)

        val frame1 = FrameAfterPartialParsing(
            ImageAfterPartialParsing(
                Image1,
                PathImage1,
                mutableListOf()
            ),
            listOf(
                OutputAfterPartialParsing(
                    Image1,
                    PathOutput1,
                    Layer,
                    LayerKind.Keypoint,
                    mutableListOf()
                )
            )
        )

        val frame2 = FrameAfterPartialParsing(
            ImageAfterPartialParsing(
                Image2,
                PathImage2,
                mutableListOf()
            ),
            listOf(
                OutputAfterPartialParsing(
                    Image2,
                    PathOutput2,
                    Layer,
                    LayerKind.Keypoint,
                    mutableListOf()
                )
            )
        )

        val expected = ProjectAfterPartialParsing(PathTestProject, listOf(frame1, frame2))

        Assertions.assertEquals(expected, result)
    }

    companion object {
        const val PathTestProject = "testData/TestProject1"
        const val Image1 = "123456789"
        const val Image2 = "987654321"
        const val Layer = "alg1_keypoint"
        const val PathImage1 = "testData/TestProject1/images/123456789.jpg"
        const val PathImage2 = "testData/TestProject1/images/987654321.jpg"
        const val PathOutput1 = "testData/TestProject1/alg1_keypoint/123456789.csv"
        const val PathOutput2 = "testData/TestProject1/alg1_keypoint/987654321.csv"
    }
}
