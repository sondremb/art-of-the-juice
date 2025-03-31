package dev.bakke.artofjuice

import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.enemy.SkaterAnimatedSprite
import dev.bakke.artofjuice.collision.CollisionSystem
import ktx.math.vec2

class EnemyAIComponent : Component() {
    private val speed = 100f // Horizontal speed

    private lateinit var animatedSprite: SkaterAnimatedSprite
    private lateinit var collisionSystem: CollisionSystem
    private lateinit var colliderComponent: ColliderComponent
    override fun lateInit() {
        entity.velocity.x = speed
        animatedSprite = getComponent()
        collisionSystem = context.inject()
        colliderComponent = getComponent()
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
