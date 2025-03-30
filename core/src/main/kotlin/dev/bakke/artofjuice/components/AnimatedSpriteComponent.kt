package dev.bakke.artofjuice.components

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.graphics.use

abstract class AnimatedSpriteComponent<TState> : Component() {
    abstract var animations: Map<TState, Animation<TextureRegion>>
    abstract var currentState: TState
    private var stateTime = 0f
    var flipX = false

    override fun update(delta: Float) {
        stateTime += delta
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val scaleX = if (flipX) -1f else 1f
        val frame = getCurrentFrame()
        batch.use {
            it.draw(
                frame,
                entity.position.x - scaleX * frame.regionWidth / 2f,
                entity.position.y - frame.regionHeight / 2f,
                frame.regionWidth.toFloat() * scaleX,
                frame.regionHeight.toFloat()
            )
        }
    }

    private fun getCurrentFrame(): TextureRegion {
        return animations[currentState]!!.getKeyFrame(stateTime, true)
    }

    fun setState(state: TState) {
        if (currentState != state) {
            currentState = state
            stateTime = 0f
        }
    }
}
