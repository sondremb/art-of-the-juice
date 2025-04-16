package dev.bakke.artofjuice.engine.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import ktx.assets.toInternalFile
import ktx.graphics.use

open class ShaderPass(
        fragShader: FileHandle,
        vertShader: FileHandle = "shaders/default.vert".toInternalFile()) : Renderpass {
    protected val shader: ShaderProgram = ShaderProgram(vertShader, fragShader)
    private val batch: SpriteBatch

    init {
        if (!shader.isCompiled) {
            Gdx.app.error("Shader", "Compilation failed:\n" + shader.log)
        }

        batch = SpriteBatch(1000, shader)
    }

    override fun render(inputTexture: Texture, buffers: PingPongBuffer) {
        beforeRender()
        ShaderProgram.pedantic = false
        shader.use {
            it.setUniformf("u_screenSize", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
        batch.projectionMatrix =
            Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        val buffer = buffers.write
        buffer.use {
            batch.use {
                it.draw(
                    inputTexture,
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
    }

    open fun beforeRender() {
    }
}

