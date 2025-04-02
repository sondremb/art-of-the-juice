package dev.bakke.artofjuice

import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.player.GunStats

class BulletComponent(private val gunStats: GunStats) : Component() {
    override fun lateInit() {
        val colliderComponent = getComponent<ColliderComponent>()
        colliderComponent.onCollision {
            if (it.hasTag(Tag.ENEMY)) {
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
