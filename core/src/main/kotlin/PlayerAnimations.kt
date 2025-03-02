import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.assets.toInternalFile

class PlayerAnimations {
    private var atlas: TextureAtlas = TextureAtlas("player/Player.atlas".toInternalFile())
    private var currentState = State.IDLE
    private var stateTime = 0f

    private var animations = mutableMapOf<State, Animation<TextureRegion>>(
        State.RUN to Animation(0.1f, atlas.findRegions("run"), Animation.PlayMode.LOOP),
        State.IDLE to Animation(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP)
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

    fun dispose() {
        atlas.dispose()
    }

    enum class State {
        RUN,
        IDLE
    }
}

fun PlayerAnimations?.disposeSafely() {
    this?.dispose()
}
