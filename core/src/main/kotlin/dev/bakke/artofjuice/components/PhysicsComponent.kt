package dev.bakke.artofjuice.components

import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.CollisionSystem
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2

class PhysicsComponent(var gravity: Float) : Component() {
    var isOnGround = false
        private set

    private lateinit var colliderComponent: ColliderComponent
    private lateinit var collisionSystem: CollisionSystem

    override fun lateInit() {
        colliderComponent = entity.getComponent()!!
        collisionSystem = context.inject()
    }

    override fun update(delta: Float) {
        // Apply gravity
        val newPosition = entity.position + (entity.velocity * delta) + vec2(0f, gravity) * (delta * delta / 2f)
        entity.velocity.y += gravity * delta

        val collider = colliderComponent.shape
        collider.setPosition(newPosition.x, entity.position.y)
        if (!collisionSystem.collidesWithTerrain(collider)) {
            entity.position.x = newPosition.x
        }
        collider.setPosition(entity.position.x, newPosition.y)
        if (collisionSystem.collidesWithTerrain(collider)) {
            isOnGround = entity.velocity.y < 0f
            entity.velocity.y = 0f
        } else {
            isOnGround = false
            entity.position.y = newPosition.y
        }
        collider.setPosition(entity.position.x, entity.position.y)
    }
}
