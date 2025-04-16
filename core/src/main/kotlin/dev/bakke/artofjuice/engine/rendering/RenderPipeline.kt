package dev.bakke.artofjuice.engine.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import ktx.graphics.use

class RenderPipeline(
    width: Int,
    height: Int,
    private val passes: List<Renderpass>
) {
    private val screenBatch = SpriteBatch()
    private val buffers = PingPongBuffer(width, height)

    fun getTexture(block: () -> Unit): Texture {
        buffers.write.use {
            block.invoke()
        }
        buffers.swap()
        return buffers.read.colorBufferTexture
    }

    fun render(inputTexture: Texture): Texture {
        // Start with input texture
        var currentTexture = inputTexture

        passes.forEach { pass ->
            // Determine output
            pass.render(currentTexture, buffers)

            // Swap for next round
            currentTexture = buffers.read.colorBufferTexture
        }

        return currentTexture
    }

    fun renderToScreen(texture: Texture) {
        screenBatch.projectionMatrix =
            Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        screenBatch.use {
            it.draw(
                texture,
                0f,
                0f,
                Gdx.graphics.width.toFloat(),
                Gdx.graphics.height.toFloat(),
                0f,
                0f,
                1f,
                1f
            )
        }
    }

    fun resize(width: Int, height: Int) {
        buffers.resize(width, height)
        passes.forEach { it.resize(width, height) }
    }

    fun dispose() {
        buffers.dispose()
    }
}
