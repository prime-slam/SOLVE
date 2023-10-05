package solve.main

import com.huskerdev.openglfx.events.GLInitializeEvent
import com.huskerdev.openglfx.events.GLRenderEvent
import com.huskerdev.openglfx.events.GLReshapeEvent
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.GL_LEQUAL
import org.lwjgl.opengl.GL11.GL_MODELVIEW
import org.lwjgl.opengl.GL11.GL_PROJECTION
import org.lwjgl.opengl.GL11.GL_QUADS
import org.lwjgl.opengl.GL11.glBegin
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glColor3f
import org.lwjgl.opengl.GL11.glDepthFunc
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL11.glEnd
import org.lwjgl.opengl.GL11.glFrustum
import org.lwjgl.opengl.GL11.glLoadIdentity
import org.lwjgl.opengl.GL11.glMatrixMode
import org.lwjgl.opengl.GL11.glNormal3f
import org.lwjgl.opengl.GL11.glPopMatrix
import org.lwjgl.opengl.GL11.glPushMatrix
import org.lwjgl.opengl.GL11.glRotatef
import org.lwjgl.opengl.GL11.glTranslatef
import org.lwjgl.opengl.GL11.glVertex2d
import org.lwjgl.opengl.GL11.glVertex3f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.glCompileShader
import solve.engine.jade.BaseScene

class ExampleRenderer {
    companion object {

        var baseScene = BaseScene()

        var animation = 0.0

        @JvmStatic
        fun canvasInit(event: GLInitializeEvent) {
            event.toString()
            createCapabilities()
            baseScene.init()
            baseScene.start()
        }

        @JvmStatic
        fun reshape(event: GLReshapeEvent){
            glMatrixMode(GL_PROJECTION)
            glLoadIdentity()

            val aspect = event.height.toDouble() / event.width
            glFrustum(-1.0, 1.0, -aspect, aspect, 5.0, 60.0)
            glMatrixMode(GL_MODELVIEW)
            glLoadIdentity()

            glTranslatef(0.0f, 0.0f, -40.0f)
        }

        @JvmStatic
        fun render(event: GLRenderEvent){
            println(event.fps)
            animation += event.delta * 100

            //val width = event.width.toDouble()
            //val height = event.height.toDouble()

            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_LEQUAL)

            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            baseScene.update(event.delta.toFloat())
        }
    }
}

