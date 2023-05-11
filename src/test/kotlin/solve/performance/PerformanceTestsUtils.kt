package solve.performance

import com.sun.javafx.perf.PerformanceTracker
import javafx.animation.AnimationTimer
import javafx.scene.Scene

fun getAvgFps(
    maxRunCount: Int,
    targetFrameRate: Int,
    scene: Scene,
    secondaryAction: () -> Unit,
    action: () -> Unit,
    isRunFinished: () -> Boolean,
    resetAction: () -> Unit,
    afterAction: () -> Unit
): Double {
    var runCount = 0
    var counter = 0
    val animationTimerFps = 60
    val performanceTracker = PerformanceTracker.getSceneTracker(scene)
    val passCount = animationTimerFps / targetFrameRate
    var avgFpsSum = 0.0
    val timer: AnimationTimer = object : AnimationTimer() {
        override fun handle(now: Long) {
            counter++
            if (counter < passCount) {
                secondaryAction()
                return
            }
            counter = 0
            action()
            if (isRunFinished()) {
                resetAction()
                runCount++
                avgFpsSum += performanceTracker.averageFPS
                performanceTracker.resetAverageFPS()
                if (maxRunCount == runCount) {
                    stop()
                    return
                }
            }
            afterAction()
        }
    }
    timer.start()

    while (maxRunCount != runCount) {
        Thread.sleep(1000)
    }
    return avgFpsSum / maxRunCount
}
