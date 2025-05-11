package dev.bakke.artofjuice.enemy

import dev.bakke.artofjuice.HealthBarComponent
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.ShockwaveSystem
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.player.ExplosionComponent

class EnemyDeathComponent : Component() {

    override fun lateInit() {
        val healthComponent = getComponent<HealthComponent>()
        val colliderComponent = getComponent<ColliderComponent>()
        val aiComponent = getComponent<EnemyAIComponent>()

        healthComponent.onDeath += {
            // Play death animation
            getComponent<EnemyAnimatedSprite>().requestTransition(EnemyAnimatedSprite.State.DEATH)
            // Remove behaviors, including health and AI
            healthComponent.isActive = false
            aiComponent.isActive = false
            // with AI disable, entity may still have some velocity, so set it to zero
            entity.velocity.setZero()
            // disable entity collisions, so bullets can pass through,
            // but leave terrain collisions enabled, so the entity can fall and not pass through the ground
            colliderComponent.disableEntityCollisions()

            // allow the healthbar to drain to zero, and then remove it from the entity
            getComponent<HealthBarComponent>().let {
                it.animationFinished += {
                    it.removeFromEntity()
                    healthComponent.removeFromEntity()
                }
            }

            // random chance to spawn an explosion on death!
            if (Math.random() < 0.3f) {
                entity.world.spawnEntity(entity.position.cpy()) {
                    +ExplosionComponent(50f, 70, screenshakeIntensity = 0.6f, knockbackIntensity = 1000f)
                }
                getSystem<ShockwaveSystem>().addExplosion(entity.position.cpy())
            }
        }
    }
}
