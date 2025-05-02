package dev.bakke.artofjuice.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.plusAssign
import ktx.math.times

interface ParticleRenderable {
    fun update(particle: Particle, delta: Float): Unit
    fun render(particle: Particle, spriteBatch: SpriteBatch): Unit
}

class SpriteRenderable(val sprite: Sprite) : ParticleRenderable {
    override fun update(particle: Particle, delta: Float) {
    }

    override fun render(particle: Particle, spriteBatch: SpriteBatch) {
        sprite.setPosition(particle.position.x, particle.position.y)
        sprite.draw(spriteBatch)
    }
}

class AnimationRenderable<T : TextureRegion>(private val animation: Animation<T>) : ParticleRenderable {
    private var stateTime = 0f
    private lateinit var frame: T
    override fun update(particle: Particle, delta: Float) {
        frame = animation.getKeyFrame(stateTime)
        stateTime += delta
    }

    override fun render(particle: Particle, spriteBatch: SpriteBatch) {
        val w = frame.regionWidth.toFloat()
        val h = frame.regionHeight.toFloat()
        spriteBatch.draw(frame, particle.position.x - w / 2f, particle.position.y - h / 2f, w, h)
        spriteBatch.color = Color.WHITE
    }
}

class Particle : Pool.Poolable {
    var renderable: ParticleRenderable? = null
    var position = Vector2()
    var velocity = Vector2()
    var lifetime = 0f
    var maxLifetime = 1f
    var active = false

    fun init(renderable: ParticleRenderable, position: Vector2, velocity :Vector2, lifetime: Float) {
        this.renderable = renderable
        this.position.set(position)
        this.velocity.set(velocity)
        this.lifetime = lifetime
        maxLifetime = lifetime
        active = true
    }

    fun update(dt: Float) {
        if (!active) {
            return
        }
        position += velocity * dt
        lifetime -= dt
        renderable?.update(this, dt)
        if (lifetime <= 0f) {
            active = false
        }
    }

    fun render(batch: SpriteBatch) {
        if (!active) return
        renderable?.render(this, batch)
    }

    override fun reset() {
        active = false
        renderable = null
    }
}
