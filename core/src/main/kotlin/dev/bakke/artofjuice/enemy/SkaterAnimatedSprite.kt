package dev.bakke.artofjuice.enemy


import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import dev.bakke.artofjuice.components.AnimatedSpriteComponent
import ktx.assets.toInternalFile

class SkaterAnimatedSprite : AnimatedSpriteComponent<SkaterAnimatedSprite.State>() {
    private var atlas = TextureAtlas("Skater.atlas".toInternalFile())
    override var currentState = State.IDLE

    override var animations = mapOf<State, Animation<TextureRegion>>(
    State.RUN to Animation(1f / 8f, atlas.findRegions("Walk"), Animation.PlayMode.LOOP),
    State.IDLE to Animation(1f / 6f, atlas.findRegions("Idle"), Animation.PlayMode.LOOP)
    )


    enum class State {
        RUN,
        IDLE
    }
}

