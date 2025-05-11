package dev.bakke.artofjuice.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.rendering.PingPongBuffer
import dev.bakke.artofjuice.engine.rendering.Renderpass
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.graphics.use

class BloomPass() : Renderpass {
    private val thresholdShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_threshold.frag".toInternalFile())
    private val gaussianShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/gaussian_m4_sigma10_00.frag".toInternalFile())
    private val addShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_add.frag".toInternalFile())
    var frameBuffer: FrameBuffer? = null
    val batch = SpriteBatch()

    override fun render(inputTexture: Texture, buffers: PingPongBuffer) {
        batch.projectionMatrix =
            Matrix4().setToOrtho2D(0f, 0f, buffers.width.toFloat(), buffers.height.toFloat())
        if (frameBuffer == null) {
            frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, buffers.width, buffers.height, false)
        }
        frameBuffer!!.use {
            batch.use {
                it.shader = null
                it.draw(inputTexture,
                    0f,
                    0f,
                    buffers.width.toFloat(),
                    buffers.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f)
            }
        }
        val inputCopy = frameBuffer!!.colorBufferTexture

        var texture = inputTexture
        thresholdShader.use {
            it.setUniformf("u_threshold", 0.8f)
        }
        texture = drawSomething(buffers, texture, thresholdShader, Color(0f, 0f, 0f, 0f))

        gaussianShader.use {
            it.setUniformf("u_screenSize", buffers.width.toFloat(), buffers.height.toFloat())
            it.setUniformf("u_direction", Vector2.X.cpy())
        }
        texture = drawSomething(buffers, texture, gaussianShader, Color(0f, 0f, 0f, 0f))

        gaussianShader.use {
            it.setUniformf("u_screenSize", buffers.width.toFloat(), buffers.height.toFloat())
            it.setUniformf("u_direction", Vector2.Y.cpy())
        }
        texture = drawSomething(buffers, texture, gaussianShader, Color(0f, 0f, 0f, 0f))

        addShader.use {
            it.setUniformf("u_intensity", 10f)
            texture.bind(1)
            it.setUniformi("u_bloom", 1)
            inputCopy.bind(0)
            it.setUniformi("u_texture", 0)
        }
        texture = drawSomething(buffers, inputCopy, addShader)
    }

    private fun drawSomething(
        buffers: PingPongBuffer,
        texture: Texture,
        shader: ShaderProgram,
        clearColor: Color? = null
    ): Texture {
        buffers.write.use {
            clearColor?.let { clearScreen(it.r, it.g, it.b, it.a) }
            batch.use {
                it.shader = shader
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffers.width.toFloat(),
                    buffers.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()
        return buffers.read.colorBufferTexture
    }

    override fun resize(width: Int, height: Int) {
        frameBuffer.disposeSafely()
        frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
    }
}
