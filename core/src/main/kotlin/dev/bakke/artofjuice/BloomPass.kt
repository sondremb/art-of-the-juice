package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.PingPongBuffer
import dev.bakke.artofjuice.engine.Renderpass
import ktx.app.clearScreen
import ktx.assets.toInternalFile
import ktx.graphics.use

class BloomPass() : Renderpass {
    val thresholdShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_threshold.frag".toInternalFile())
    val gaussianShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_blur.frag".toInternalFile())
    val addShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_add.frag".toInternalFile())
    val thresholdBatch = SpriteBatch(1000, thresholdShader)
    val gaussianBatch = SpriteBatch(1000, gaussianShader)
    val addBatch = SpriteBatch(1000, addShader)

    override fun render(inputTexture: Texture, buffers: PingPongBuffer) {
        val inputCopyBuffer = FrameBuffer(Pixmap.Format.RGBA8888, inputTexture.width, inputTexture.height, false)
        val inputCopyBatch = SpriteBatch()
        inputCopyBuffer.use {  buffer ->
            inputCopyBatch.use {
                it.draw(inputTexture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f)
            }
        }
        val inputCopy = inputCopyBuffer.colorBufferTexture
        inputCopyBatch.dispose()

        var texture = inputTexture
        var buffer = buffers.write
        thresholdShader.use {
            it.setUniformf("u_threshold", 0.8f)
        }
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            thresholdBatch.use {
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()

        texture = buffers.read.colorBufferTexture
        gaussianShader.use {
            it.setUniformf("u_screenSize", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            it.setUniformf("u_direction", Vector2.Y.cpy())
        }
        buffer = buffers.write
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            gaussianBatch.use {
                clearScreen(1f, 1f, 1f, 0f)
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()

        texture = buffers.read.colorBufferTexture
        gaussianShader.use {
            it.setUniformf("u_screenSize", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            it.setUniformf("u_direction", Vector2.X.cpy())
        }
        buffer = buffers.write
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            gaussianBatch.use {
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()

        texture = buffers.read.colorBufferTexture
        addShader.use {
            it.setUniformf("u_intensity", 5f)
            texture.bind(1)
            it.setUniformi("u_bloom", 1)
            inputCopy.bind(0)
            it.setUniformi("u_texture", 0)
        }
        buffer = buffers.write
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            addBatch.use {
                it.draw(
                    inputCopy,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        texture = buffer.colorBufferTexture
        buffers.swap()
    }
}
