package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class AnimatedSpriteComponent<TState>(
) {
    abstract var animations: Map<TState, Animation<TextureRegion>>
    abstract var currentState: TState
    private var stateTime = 0f
    private var isFacingRight = true

    fun update(entity: Entity, delta: Float) {
        stateTime += delta
        if (entity.velocity.x < 0f) {
            isFacingRight = false
        } else if (entity.velocity.x > 0f) {
            isFacingRight = true
        }
    }

    fun render(entity: Entity, spriteBatch: Batch) {
        val scaleX = if (isFacingRight) 1f else -1f
        val frame = getCurrentFrame()
        spriteBatch.draw(
            frame,
            entity.position.x + if (isFacingRight) 0f else frame.regionWidth.toFloat(),
            entity.position.y,
            frame.regionWidth.toFloat() * scaleX,
            frame.regionHeight.toFloat()
        )
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
