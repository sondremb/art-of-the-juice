package dev.bakke.artofjuice.enemy

import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import ktx.math.vec2
import kotlin.math.sign

class EnemyAIComponent(private var direction: Float = 1f, private var speed: Float) : Component() {
    private lateinit var animatedSprite: EnemyAnimatedSprite
    private lateinit var collisionSystem: CollisionSystem
    private lateinit var colliderComponent: ColliderComponent
    override fun lateInit() {
        entity.velocity.x = speed * sign(direction)
        animatedSprite = getComponent()
        collisionSystem = getSystem()
        colliderComponent = getComponent()
        getComponent<HealthComponent>().onDamage += {
            animatedSprite.requestTransition(EnemyAnimatedSprite.State.HURT)
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
        animatedSprite.requestTransition(EnemyAnimatedSprite.State.RUN)
    }
}
