package sliv.tool.main

import sliv.tool.catalogue.controller.CatalogueController
import sliv.tool.project.model.*
import sliv.tool.scene.SceneFacade
import sliv.tool.scene.controller.SceneController
import sliv.tool.utils.ServiceLocator
import tornadofx.*
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId

class MainController : Controller() {
    private val sceneController: SceneController by inject()
    val sceneFacade = SceneFacade(sceneController).also { ServiceLocator.registerService(it) }
    private val catalogueController: CatalogueController by inject()

    //TODO: temporary solution to fill scene with data
    fun importTestData() {
        val layer = ProjectLayer(LayerKind.Keypoint, "ORB")
        val path = Paths.get("test/data/room.jpg")
        val currentTimestamp = {
            LocalDateTime.now().atZone(ZoneId.of("UTC+03:00")).toInstant().toEpochMilli()
        }
        val landmarks = listOf(LandmarkFile(layer, Paths.get("test/data/points.csv"), emptyList()))
        val frames = (1..30000).map { ProjectFrame(currentTimestamp(), path, landmarks) }
        catalogueController.setCatalogueFrames(frames)
    }
}
