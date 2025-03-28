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
    private var isFacingRight = true

    override fun update(delta: Float) {
        stateTime += delta
        if (entity.velocity.x < 0f) {
            isFacingRight = false
        } else if (entity.velocity.x > 0f) {
            isFacingRight = true
        }
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val scaleX = if (isFacingRight) 1f else -1f
        val frame = getCurrentFrame()
        batch.use {
            it.draw(
                frame,
                entity.position.x + if (isFacingRight) 0f else frame.regionWidth.toFloat(),
                entity.position.y,
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
