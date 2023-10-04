package solve.main

import com.huskerdev.openglfx.events.GLRenderEvent
import com.huskerdev.openglfx.events.GLReshapeEvent
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

class ExampleRenderer {
    companion object {
        var animation = 0.0

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

            val width = event.width.toDouble()
            val height = event.height.toDouble()

            glEnable(GL_DEPTH_TEST)
            glDepthFunc(GL_LEQUAL)

            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            glBegin(GL_QUADS)
            glColor3f(1.0f, 0.5f, 0.0f)
            glVertex2d(-10.0, 0.0)
            glVertex2d(width, 0.0)
            glVertex2d(width, height / 2)
            glVertex2d(-10.0, height / 2)
            glEnd()

            // Moving rectangle
            val rectSize = 2f

            glPushMatrix()
            glRotatef(animation.toFloat(), 1f, 0f, 0f)
            glRotatef(animation.toFloat(), 0f, 1f, 0f)

            glBegin(GL_QUADS)
            // top
            glColor3f(1.0f, 0.0f, 0.0f)
            glNormal3f(0.0f, 1.0f, 0.0f)
            glVertex3f(-rectSize, rectSize, rectSize)
            glVertex3f(rectSize, rectSize, rectSize)
            glVertex3f(rectSize, rectSize, -rectSize)
            glVertex3f(-rectSize, rectSize, -rectSize)

            // front
            glColor3f(0.0f, 1.0f, 0.0f)
            glNormal3f(0.0f, 0.0f, 1.0f)
            glVertex3f(rectSize, -rectSize, rectSize)
            glVertex3f(rectSize, rectSize, rectSize)
            glVertex3f(-rectSize, rectSize, rectSize)
            glVertex3f(-rectSize, -rectSize, rectSize)

            // right
            glColor3f(0.0f, 0.0f, 1.0f)
            glNormal3f(1.0f, 0.0f, 0.0f)
            glVertex3f(rectSize, rectSize, -rectSize)
            glVertex3f(rectSize, rectSize, rectSize)
            glVertex3f(rectSize, -rectSize, rectSize)
            glVertex3f(rectSize, -rectSize, -rectSize)

            // left
            glColor3f(0.0f, 0.0f, 0.5f)
            glNormal3f(1.0f, 0.0f, 0.0f)
            glVertex3f(-rectSize, -rectSize, rectSize)
            glVertex3f(-rectSize, rectSize, rectSize)
            glVertex3f(-rectSize, rectSize, -rectSize)
            glVertex3f(-rectSize, -rectSize, -rectSize)

            // bottom
            glColor3f(0.5f, 0.0f, 0.0f)
            glNormal3f(0.0f, 1.0f, 0.0f)
            glVertex3f(rectSize, -rectSize, rectSize)
            glVertex3f(-rectSize, -rectSize, rectSize)
            glVertex3f(-rectSize, -rectSize, -rectSize)
            glVertex3f(rectSize, -rectSize, -rectSize)

            // back
            glColor3f(0.0f, 0.5f, 0.0f)
            glNormal3f(0.0f, 0.0f, 1.0f)
            glVertex3f(rectSize, rectSize, -rectSize)
            glVertex3f(rectSize, -rectSize, -rectSize)
            glVertex3f(-rectSize, -rectSize, -rectSize)
            glVertex3f(-rectSize, rectSize, -rectSize)

            glEnd()
            glPopMatrix()
            val sp = 5f
            glBegin(GL_QUADS)
            glColor3f(0.5f, 0.5f, 0.5f)
            glVertex3f(-sp, -sp, 0.0f)
            glVertex3f(sp, -sp, 0.0f)
            glVertex3f(sp, sp, 0.0f)
            glVertex3f(-sp, sp, 0.0f)
            glEnd()

            glPopMatrix()
        }
    }
}

