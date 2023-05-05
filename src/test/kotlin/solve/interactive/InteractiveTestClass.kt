package solve.interactive

import org.junit.jupiter.api.BeforeAll

internal open class InteractiveTestClass {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpAll() {
            System.setProperty("testfx.robot", "glass")
            System.setProperty("testfx.headless", "true")
            System.setProperty("prism.order", "sw")
            System.setProperty("prism.text", "t2k")
        }
    }
}
