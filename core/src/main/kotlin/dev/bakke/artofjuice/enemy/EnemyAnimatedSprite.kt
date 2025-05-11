package dev.bakke.artofjuice.enemy


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.Assets
import dev.bakke.artofjuice.TextureAssets
import dev.bakke.artofjuice.engine.components.Component
import ktx.collections.toGdxArray
import ktx.graphics.use

class EnemyAnimatedSprite : Component() {
    private var currentState = State.IDLE
    private var nextState: State? = null
    private lateinit var currentAnimation: Animation<TextureRegion>
    private var stateTime = 0f
    var flipX = false

    private lateinit var assets: Assets
    override fun lateInit() {
        assets = getSystem()
    }

    fun requestTransition(state: EnemyAnimatedSprite.State) {
        if (currentState == state) return
        val transition = transitions[currentState]?.get(state)
        when (transition) {
            true -> {
                currentState = state
                nextState = null
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
            State.RUN -> Animation(1f / 8f, assets.getRegions(TextureAssets.Enemy.Walk), Animation.PlayMode.LOOP)
            State.IDLE -> Animation(1f / 6f, assets.getRegions(TextureAssets.Enemy.Idle), Animation.PlayMode.LOOP)
            State.HURT -> Animation(1f / 8f, assets.getRegions(TextureAssets.Enemy.Hurt).drop(1).toGdxArray(), Animation.PlayMode.NORMAL)
            State.DEATH -> Animation(1f / 8f, assets.getRegions(TextureAssets.Enemy.Death), Animation.PlayMode.NORMAL)
        }
    }

    override fun update(delta: Float) {
        stateTime += delta
        currentAnimation = getCurrentAnimation()
        if (currentAnimation.isAnimationFinished(stateTime) && nextState != null) {
            currentState = nextState!!
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
            State.HURT to true,
            State.DEATH to true
        ),
        State.RUN to mapOf(
            State.IDLE to true,
            State.HURT to true,
            State.DEATH to true
        ),
        State.HURT to mapOf(
            State.RUN to false,
            State.IDLE to false,
            State.HURT to true,
            State.DEATH to true
        ), State.DEATH to mapOf()
    )

    enum class State {
        RUN,
        IDLE,
        HURT,
        DEATH
    }
}

