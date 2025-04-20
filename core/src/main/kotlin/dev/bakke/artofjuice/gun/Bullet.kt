package dev.bakke.artofjuice.gun

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.engine.AnimationRenderable
import dev.bakke.artofjuice.engine.ParticleSystem
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import ktx.assets.toInternalFile
import ktx.math.vec2

class BulletComponent(private val gunStats: GunStats) : Component() {
    override fun lateInit() {
        val colliderComponent = getComponent<ColliderComponent>()
        colliderComponent.onCollision {
            if (it.hasTag(Tag.ENEMY)) {
                it.getComponent<HealthComponent>().damage(gunStats.damage)
                it.getComponent<PhysicsComponent>().applyImpulse(vec2(entity.velocity.x, entity.velocity.x * 0.2f), gunStats.impulse)
                val number = (Math.random() * 5).toInt() + 6
                val animation = TextureAtlas("Effects.atlas".toInternalFile())
                    .findRegions("effect$number")
                    .let { Animation(1/24f, it) }
                getSystem<ParticleSystem>().spawn(AnimationRenderable(animation), entity.position.cpy(), Vector2.Zero.cpy(), 0.2f)
                entity.destroy()
            }
        }
        colliderComponent.onTerrainCollision {
            val number = (Math.random() * 5).toInt() + 6
            val animation = TextureAtlas("Effects.atlas".toInternalFile())
                .findRegions("effect$number")
                .let { Animation(1/24f, it) }
            getSystem<ParticleSystem>().spawn(AnimationRenderable(animation), entity.position.cpy(), Vector2.Zero.cpy(), 0.2f)
            entity.destroy()
        }
    }

    override fun update(delta: Float) {
        entity.position.x += entity.velocity.x * delta
        entity.position.y += entity.velocity.y * delta
    }
}
