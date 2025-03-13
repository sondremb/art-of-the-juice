package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile

class SkaterAnimations: Disposable {
    private var atlas: TextureAtlas = TextureAtlas("Skater.atlas".toInternalFile())
    private var currentState = State.IDLE
    private var stateTime = 0f

    private var animations = mutableMapOf<State, Animation<TextureRegion>>(
        State.RUN to Animation(1f / 8f, atlas.findRegions("Walk"), Animation.PlayMode.LOOP),
        State.IDLE to Animation(1f / 6f, atlas.findRegions("Idle"), Animation.PlayMode.LOOP)
    )

    fun update(delta: Float) {
        stateTime += delta
    }

    fun getCurrentFrame(): TextureRegion {
        return animations[currentState]!!.getKeyFrame(stateTime, true)
    }

    fun setState(state: State) {
        if (currentState != state) {
            currentState = state
            stateTime = 0f
        }
    }

    override fun dispose() {
        atlas.disposeSafely()
    }

    enum class State {
        RUN,
        IDLE
    }
}

