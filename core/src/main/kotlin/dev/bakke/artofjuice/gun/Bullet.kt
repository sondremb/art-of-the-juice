package dev.bakke.artofjuice.gun

import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import ktx.math.vec2

class BulletComponent(private val gunStats: GunStats) : Component() {
    override fun lateInit() {
        val colliderComponent = getComponent<ColliderComponent>()
        colliderComponent.onCollision {
            if (it.hasTag(Tag.ENEMY)) {
                it.getComponent<HealthComponent>().damage(gunStats.damage)
                it.getComponent<PhysicsComponent>().applyImpulse(vec2(entity.velocity.x, entity.velocity.x * 0.2f), gunStats.impulse)
                entity.destroy()
            }
        }
        colliderComponent.onTerrainCollision {
            entity.destroy()
        }
    }

    override fun update(delta: Float) {
        entity.position.x += entity.velocity.x * delta
        entity.position.y += entity.velocity.y * delta
    }
}
