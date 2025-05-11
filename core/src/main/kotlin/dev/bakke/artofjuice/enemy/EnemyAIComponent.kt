package dev.bakke.artofjuice.enemy

import dev.bakke.artofjuice.HealthBarComponent
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.ShockwaveSystem
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.player.ExplosionComponent
import ktx.math.vec2
import kotlin.math.sign

class EnemyAIComponent(private var direction: Float = 1f) : Component() {
    private val speed = 100f // Horizontal speed

    private lateinit var animatedSprite: EnemyAnimtedSprite
    private lateinit var collisionSystem: CollisionSystem
    private lateinit var colliderComponent: ColliderComponent
    override fun lateInit() {
        entity.velocity.x = speed * sign(direction)
        animatedSprite = getComponent()
        collisionSystem = getSystem()
        colliderComponent = getComponent()
        val healthComponent = getComponent<HealthComponent>()
        healthComponent.onDeath += {
            animatedSprite.requestTransition(EnemyAnimtedSprite.State.DEATH)
            healthComponent.isActive = false
            this.isActive = false
            colliderComponent.isActive = false
            colliderComponent.disableEntityCollisions()
            getComponent<HealthBarComponent>().let {
                it.animationFinished += {
                    it.removeFromEntity()
                    healthComponent.removeFromEntity()
                }
            }
            entity.velocity.setZero()
            if (Math.random() < 0.3f) {
                entity.world.spawnEntity(entity.position.cpy()) {
                    +ExplosionComponent(50f, 70, screenshakeIntensity = 0.6f, knockbackIntensity = 1000f)
                }
                getSystem<ShockwaveSystem>().addExplosion(entity.position.cpy())
            }
        }
        healthComponent.onDamage += {
            animatedSprite.requestTransition(EnemyAnimtedSprite.State.HURT)
        }
    }

    override fun update(delta: Float) {
        if (!isActive) return
        val nextPosition = vec2(entity.position.x + entity.velocity.x * delta, entity.position.y)
        colliderComponent.shape.setPosition(nextPosition.x, nextPosition.y)
        if (collisionSystem.collidesWithTerrain(colliderComponent.shape)) {
            entity.velocity.x = -entity.velocity.x
        }
        colliderComponent.resetPosition()
        animatedSprite.flipX = entity.velocity.x < 0
        animatedSprite.requestTransition(EnemyAnimtedSprite.State.RUN)
    }
}
