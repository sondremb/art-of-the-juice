package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dev.bakke.artofjuice.components.AnimatedSpriteComponent
import ktx.assets.toInternalFile

class PlayerAnimatedSprite : AnimatedSpriteComponent<PlayerAnimatedSprite.State>() {
    private var atlas = TextureAtlas("player/Player.atlas".toInternalFile())
    override var currentState = State.IDLE

    override var animations = mapOf<State, Animation<TextureRegion>>(
        State.RUN to Animation(1f / 8f, atlas.findRegions("run"), Animation.PlayMode.LOOP),
        State.IDLE to Animation(1f / 6f, atlas.findRegions("idle"), Animation.PlayMode.LOOP)
    )

    enum class State {
        RUN,
        IDLE
    }
}
