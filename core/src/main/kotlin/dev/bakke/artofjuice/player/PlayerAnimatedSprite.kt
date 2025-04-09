package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dev.bakke.artofjuice.components.AnimatedSpriteComponent
import ktx.assets.toInternalFile

class PlayerAnimatedSprite : AnimatedSpriteComponent<PlayerAnimatedSprite.State>() {
    private var atlas = TextureAtlas("new_character.atlas".toInternalFile())
    override var currentState = State.IDLE

    override var animations = mapOf<State, Animation<TextureRegion>>(
        State.RUN to Animation(1f / 8f, atlas.findRegions("both_hands_run"), Animation.PlayMode.LOOP),
        State.IDLE to Animation(1f / 6f, atlas.findRegions("both_hands_idle"), Animation.PlayMode.LOOP),
        State.JUMP to Animation(1f / 8f, atlas.findRegions("both_hands_jump"), Animation.PlayMode.NORMAL),
        State.HURT to Animation(1f / 6f, atlas.findRegions("hurt"), Animation.PlayMode.NORMAL),
    )

    enum class State {
        RUN,
        IDLE,
        JUMP,
        HURT,
    }
}
