package dev.bakke.artofjuice.enemy

import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.player.ExplosionComponent
import ktx.math.vec2
import kotlin.math.sign

class EnemyAIComponent(private var direction: Float = 1f) : Component() {
    private val speed = 100f // Horizontal speed

    private lateinit var animatedSprite: SkaterAnimatedSprite
    private lateinit var collisionSystem: CollisionSystem
    private lateinit var colliderComponent: ColliderComponent
    override fun lateInit() {
        entity.velocity.x = speed * sign(direction)
        animatedSprite = getComponent()
        collisionSystem = context.inject()
        colliderComponent = getComponent()
        getComponent<HealthComponent>().onDeath += {
            if (Math.random() < 0.2f) {
                entity.world.spawnEntity(entity.position.cpy()) {
                    +ExplosionComponent(50f, 70, screenshakeIntensity = 0.6f, knockbackIntensity = 1000f)
                }
            }
        }
    }

    override fun update(delta: Float) {
        val nextPosition = vec2(entity.position.x + entity.velocity.x * delta, entity.position.y)
        colliderComponent.shape.setPosition(nextPosition.x, nextPosition.y)
        if (collisionSystem.collidesWithTerrain(colliderComponent.shape)) {
            entity.velocity.x = -entity.velocity.x
        }
        colliderComponent.resetPosition()
        animatedSprite.flipX = entity.velocity.x < 0
        animatedSprite.setState(SkaterAnimatedSprite.State.RUN)
    }
}
