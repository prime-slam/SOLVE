package tests

import javafx.scene.control.TreeItem
import org.junit.Test
import solve.importer.ProjectParser.createTreeWithFiles
import solve.importer.ProjectParser.partialParseDirectory
import solve.importer.model.*
import solve.project.model.*

class ImporterTests {
    @Test
    fun treeCreateTest() {
        val initTree = TreeItem(FileInTree(FileInfo()))

        val project = partialParseDirectory("test/TestProject1")
        val result = project?.let { createTreeWithFiles(it, initTree) }

        val image1 = TreeItem(FileInTree(FileInfo("123456789")))
        val image2 = TreeItem(FileInTree(FileInfo("987654321")))

        image1.children.add(TreeItem(FileInTree(FileInfo("alg1_keypoint"))))
        image2.children.add(TreeItem(FileInTree(FileInfo("alg1_keypoint"))))

        initTree.children.addAll(image1, image2)

        result?.let { assert(it == initTree) }
    }

    @Test
    fun partialParseDirectoryTest() {

        val result = partialParseDirectory("test/TestProject1")

        val frame1 = FrameAfterPartialParsing(
            ImageAfterPartialParsing(
                "123456789",
                "/home/anastasia/IdeaProjects/SLIV/test/TestProject1/images/123456789.jpg"
            ),
            listOf(
                OutputAfterPartialParsing(
                    "123456789",
                    "/home/anastasia/IdeaProjects/SLIV/test/TestProject1/alg1_keypoint/123456789.csv",
                    "alg1_keypoint",
                    LayerKind.Keypoint
                )
            )
        )

        val frame2 = FrameAfterPartialParsing(
            ImageAfterPartialParsing(
                "987654321",
                "/home/anastasia/IdeaProjects/SLIV/test/TestProject1/images/987654321.jpg"
            ),
            listOf(
                OutputAfterPartialParsing(
                    "987654321",
                    "/home/anastasia/IdeaProjects/SLIV/test/TestProject1/alg1_keypoint/987654321.csv",
                    "alg1_keypoint",
                    LayerKind.Keypoint
                )
            )
        )

        val expected = ProjectAfterPartialParsing("test/TestProject1", listOf(frame1, frame2))

        result?.let { assert(it == expected) }
    }
}