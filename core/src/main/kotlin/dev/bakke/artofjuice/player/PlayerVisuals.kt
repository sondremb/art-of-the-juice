package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.gun.GunComponent
import dev.bakke.artofjuice.gun.PlayerArms
import ktx.assets.toInternalFile
import ktx.collections.toGdxArray
import ktx.graphics.use
import ktx.math.vec2

class PlayerVisuals : Component() {
    private val playerAtlas = TextureAtlas("new_character.atlas".toInternalFile())
    private var currentState = State.IDLE
    private lateinit var currentAnimation: Animation<TextureRegion>
    private var nextState: State? = null
    private var stateTime = 0f
    var flipX = false
    private val scaleX
        get() = if (flipX) -1f else 1f
    private val arm1 = Sprite(playerAtlas.findRegion("arm1_forward"))
    private val arm2 = Sprite(playerAtlas.findRegion("arm2_forward"))

    private val transitions = mapOf(
        State.IDLE to mapOf(
            State.RUN to true,
            State.JUMP to true,
            State.FALL to true,
            State.HURT to true,
        ),
        State.RUN to mapOf(
            State.IDLE to true,
            State.JUMP to true,
            State.FALL to true,
            State.HURT to true,
        ),
        State.JUMP to mapOf(
            State.IDLE to false,
            State.RUN to false,
            State.FALL to true,
            State.HURT to true,
        ),
        State.FALL to mapOf(
            State.IDLE to false,
            State.RUN to false,
            State.JUMP to true,
            State.HURT to true,
        ),
        State.HURT to mapOf(
            State.IDLE to false,
            State.RUN to false,
            State.JUMP to false,
            State.FALL to false,
        ),
    )

    fun requestTransition(state: State) {
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

    private lateinit var gunComponent: GunComponent
    override fun lateInit() {
        gunComponent = getComponent()
    }

    override fun update(delta: Float) {
        stateTime += delta
        currentAnimation = getCurrentBodyAnimation()
        if (currentAnimation.isAnimationFinished(stateTime) && nextState != null) {
            currentState = nextState ?: currentState
            nextState = null
            stateTime = 0f
            currentAnimation = getCurrentBodyAnimation()
        }
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        batch.use {
            if (gunComponent.gun?.visuals?.arms == PlayerArms.One) {
                drawOneArm(batch)
            }
            drawCurrentBodySprite(it)
            drawCurrentGunSprite(it)
            if (gunComponent.gun?.visuals?.arms == PlayerArms.Two) {
                drawTwoArms(batch)
            }
        }
    }

    private fun drawOneArm(batch: SpriteBatch) {
        val offset = vec2(6f * scaleX, 2f)
        arm1.setCenter(entity.position.x + offset.x, entity.position.y + offset.y)
        arm1.setFlip(flipX, false)
        arm1.draw(batch)
    }

    private fun drawTwoArms(batch: SpriteBatch) {
        val offset = vec2(4f * scaleX, 1f)
        arm2.setCenter(entity.position.x + offset.x, entity.position.y + offset.y)
        arm2.setFlip(flipX, false)
        arm2.draw(batch)
    }

    private fun drawCurrentBodySprite(batch: SpriteBatch) {
        val frame = currentAnimation.getKeyFrame(stateTime)
        batch.draw(
            frame,
            entity.position.x - scaleX * frame.regionWidth / 2f,
            entity.position.y - frame.regionHeight / 2f,
            frame.regionWidth.toFloat() * scaleX,
            frame.regionHeight.toFloat()
        )
    }

    private fun drawCurrentGunSprite(batch: SpriteBatch) {
        gunComponent.gun?.visuals?.let {
            val gunSprite = it.sprite
            val offset = it.gunOffset
            gunSprite.setCenter(entity.position.x + offset.x * scaleX, entity.position.y + offset.y)
            gunSprite.setFlip(flipX, false)
            gunSprite.draw(batch)
        }
    }

    private fun getCurrentBodyAnimation(): Animation<TextureRegion> {
        return when (currentState) {
            State.IDLE -> {
                when (gunComponent.gun?.visuals?.arms) {
                    null -> playerAtlas.findRegions("both_hands_idle")
                    PlayerArms.One -> playerAtlas.findRegions("one_hand_idle")
                    PlayerArms.Two -> playerAtlas.findRegions("no_hands_idle")
                }.let { Animation(1f / 6f, it, Animation.PlayMode.LOOP) }
            }
            State.RUN -> {
                when (gunComponent.gun?.visuals?.arms) {
                    null -> playerAtlas.findRegions("both_hands_run")
                    PlayerArms.One -> playerAtlas.findRegions("one_hand_walk")
                    PlayerArms.Two -> playerAtlas.findRegions("no_hands_walk")
                }.let { Animation(1f / 8f, it, Animation.PlayMode.LOOP) }
            }
            State.JUMP -> {
                when (gunComponent.gun?.visuals?.arms) {
                    null -> playerAtlas.findRegions("both_hands_jump")
                    PlayerArms.One -> playerAtlas.findRegions("one_hand_jump")
                    PlayerArms.Two -> playerAtlas.findRegions("no_hands_jump")
                }.let { Animation(1f / 8f, it, Animation.PlayMode.NORMAL) }
            }
            State.FALL -> {
                when (gunComponent.gun?.visuals?.arms) {
                    null -> playerAtlas.findRegions("both_hands_jump").toArray().takeLast(2)
                    PlayerArms.One -> playerAtlas.findRegions("one_hand_jump").toArray().takeLast(2)
                    PlayerArms.Two -> playerAtlas.findRegions("no_hands_jump").toArray().takeLast(2)
                }.let { Animation(1f / 8f, it.toGdxArray(), Animation.PlayMode.NORMAL) }
            }
            State.HURT -> Animation(1f / 6f, playerAtlas.findRegions("hurt"), Animation.PlayMode.NORMAL)
        }
    }

    enum class State {
        RUN,
        IDLE,
        JUMP,
        FALL,
        HURT,
    }
}
