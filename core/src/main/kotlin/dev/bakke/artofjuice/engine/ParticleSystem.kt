package dev.bakke.artofjuice.engine

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import ktx.graphics.use

class ParticleSystem : Disposable {
    private val pool = object : Pool<Particle>(200) {
        override fun newObject() = Particle()
    }

    private val activeParticles: MutableList<Particle> = mutableListOf()

    fun spawn(renderable: ParticleRenderable, position: Vector2, velocity: Vector2, lifetime: Float) {
        val particle = pool.obtain()
        particle.init(renderable, position, velocity, lifetime)
        activeParticles.add(particle)
    }

    fun update(dt: Float) {
        val iterator = activeParticles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.update(dt)
            if (!p.active) {
                iterator.remove()
                pool.free(p)
            }
        }
    }

    fun render(batch: SpriteBatch) {
        batch.use {
            activeParticles.forEach {
                it.render(batch)
            }
        }
    }

    override fun dispose() {
        pool.clear()
        activeParticles.clear()
    }
}
