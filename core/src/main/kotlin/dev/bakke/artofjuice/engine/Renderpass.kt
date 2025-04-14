package dev.bakke.artofjuice.engine

import com.badlogic.gdx.graphics.Texture

interface Renderpass {
    fun render(inputTexture: Texture, buffers: PingPongBuffer)
}
