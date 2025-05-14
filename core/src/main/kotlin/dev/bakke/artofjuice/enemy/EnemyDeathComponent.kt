package dev.bakke.artofjuice.enemy

import dev.bakke.artofjuice.HealthBarComponent
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.player.ExplosionComponent

class EnemyDeathComponent : Component() {
    val healthComponent: HealthComponent by getComponentLazy<HealthComponent>()
    override fun lateInit() {
        healthComponent.onDeath += ::onDeath
    }

    private fun onDeath() {
        // Play death animation
        val animatedSprite = getComponent<EnemyAnimatedSprite>()
        animatedSprite.requestTransition(EnemyAnimatedSprite.State.DEATH)
        // Remove behaviors, including health and AI
        healthComponent.isActive = false
        val aiComponent = getComponent<EnemyAIComponent>()
        aiComponent.isActive = false
        // with AI disabled, entity may still have some velocity, so set it to zero
        entity.velocity.setZero()
        // disable entity collisions, so bullets can pass through,
        // but leave terrain collisions enabled, so the entity can fall and not pass through the ground
        getComponent<ColliderComponent>().disableEntityCollisions()

        // allow the healthbar to drain to zero, and then remove it from the entity
        val healthBarComponent = getComponent<HealthBarComponent>()
        healthBarComponent.animationFinished  += {
            healthComponent.removeFromEntity()
            healthBarComponent.removeFromEntity()
        }

        if (Math.random() < 0.3f) {
            spawnEntity(entity.position.cpy()) {
                +ExplosionComponent(explosionRadius = 50f, damage = 40, knockbackIntensity = 1000f)
            }
        }
    }
}
