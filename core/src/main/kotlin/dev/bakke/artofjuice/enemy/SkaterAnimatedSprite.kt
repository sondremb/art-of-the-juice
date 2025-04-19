package dev.bakke.artofjuice.enemy


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.engine.components.AnimatedSpriteComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.player.PlayerVisuals.State
import ktx.assets.toInternalFile
import ktx.collections.toGdxArray
import ktx.graphics.use

class SkaterAnimatedSprite : Component() {
    private var atlas = TextureAtlas("Skater.atlas".toInternalFile())
    private var currentState = State.IDLE
    private var nextState: State? = null
    private lateinit var currentAnimation: Animation<TextureRegion>
    private var stateTime = 0f
    var flipX = false

    var animations = mapOf<State, Animation<TextureRegion>>(
        State.RUN to Animation(1f / 8f, atlas.findRegions("Walk"), Animation.PlayMode.LOOP),
        State.IDLE to Animation(1f / 6f, atlas.findRegions("Idle"), Animation.PlayMode.LOOP),
        State.HURT to Animation(1f / 8f, atlas.findRegions("Hurt"), Animation.PlayMode.NORMAL)
    )

    fun requestTransition(state: SkaterAnimatedSprite.State) {
        if (currentState == state) return
        val transition = transitions[currentState]?.get(state)
        when (transition) {
            true -> {
                currentState = state
                stateTime = 0f
            }
            false -> {
                nextState = state
            }
            null -> {
                // invalid state transition
            }
        }
    }

    private fun getCurrentAnimation(): Animation<TextureRegion> {
        return when (currentState) {
            State.RUN -> Animation(1f / 8f, atlas.findRegions("Walk"), Animation.PlayMode.LOOP)
            State.IDLE -> Animation(1f / 6f, atlas.findRegions("Idle"), Animation.PlayMode.LOOP)
            State.HURT -> Animation(1f / 8f, atlas.findRegions("Hurt").drop(1).toGdxArray(), Animation.PlayMode.NORMAL)
        }
    }

    override fun update(delta: Float) {
        stateTime += delta
        currentAnimation = getCurrentAnimation()
        if (currentAnimation.isAnimationFinished(stateTime) && nextState != null) {
            currentState = nextState ?: currentState
            nextState = null
            stateTime = 0f
            currentAnimation = getCurrentAnimation()
        }
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val scaleX = if (flipX) -1f else 1f
        val frame = currentAnimation.getKeyFrame(stateTime)
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

    private val transitions = mapOf(
        State.IDLE to mapOf(
            State.RUN to true,
            State.HURT to true
        ),
        State.RUN to mapOf(
            State.IDLE to true,
            State.HURT to true
        ),
        State.HURT to mapOf(
            State.RUN to false,
            State.IDLE to false,
            State.HURT to true
        )
    )

    enum class State {
        RUN,
        IDLE,
        HURT
    }
}

