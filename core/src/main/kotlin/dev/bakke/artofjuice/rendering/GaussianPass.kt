package dev.bakke.artofjuice.rendering

import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.rendering.ShaderPass
import ktx.assets.toInternalFile
import ktx.graphics.use

class GaussianPass(isY: Boolean) :
    ShaderPass("shaders/bloom_blur.frag".toInternalFile()) {
    private var direction = if (isY) Vector2.Y.cpy() else Vector2.X.cpy()

    override fun beforeRender() {
        shader.use {
            it.setUniformf("u_direction", direction)
        }
    }
}
