package dev.bakke.artofjuice.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import dev.bakke.artofjuice.ShockwaveSystem
import dev.bakke.artofjuice.engine.rendering.PingPongBuffer
import dev.bakke.artofjuice.engine.rendering.Renderpass
import ktx.assets.toInternalFile
import ktx.graphics.use
import ktx.math.vec2

class ShockwavePass(private val shockwaveSystem: ShockwaveSystem, private val camera: Camera) : Renderpass {
    private val shader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/shockwave.frag".toInternalFile())
    private val batch: SpriteBatch

    init {
        if (!shader.isCompiled) {
            Gdx.app.error("Shader", "Compilation failed:\n" + shader.log)
        }

        batch = SpriteBatch(1000, shader)
    }

    private fun worldToUV(world: Vector2): Vector2 {
        // 1. Project world coords to screen space
        val projected = camera.project(Vector3(world.x, world.y, 0f))

        // 2. Normalize to UV [0, 1]
        val uvX = projected.x / Gdx.graphics.width
        val uvY = projected.y / Gdx.graphics.height

        return vec2(uvX, uvY)
    }

    override fun render(inputTexture: Texture, buffers: PingPongBuffer) {
        shader.use {
            it.setUniformf("u_screenSize", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
        shockwaveSystem.shockwaves.take(5).forEach { shockwave ->
            shader.use {
                it.setUniformf("u_time", shockwave.time)
                it.setUniformf("u_maxTime", shockwave.duration)
                it.setUniformf("u_center", worldToUV(shockwave.position))
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
    }
}
