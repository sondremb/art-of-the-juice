package dev.bakke.artofjuice.engine.rendering

import com.badlogic.gdx.graphics.Texture

interface Renderpass {
    fun render(inputTexture: Texture, buffers: PingPongBuffer)
    fun resize(width: Int, height: Int) {}
}
