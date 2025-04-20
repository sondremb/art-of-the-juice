package dev.bakke.artofjuice.engine.components

import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import kotlin.math.exp

class PhysicsComponent(var gravity: Float) : Component() {
    var isOnGround = false
        private set

    private var impulse = vec2(0f, 0f)

    private lateinit var colliderComponent: ColliderComponent
    private lateinit var collisionSystem: CollisionSystem

    override fun lateInit() {
        colliderComponent = getComponent()
        collisionSystem = getSystem()
    }

    override fun update(delta: Float) {
        val velocity = entity.velocity.cpy().add(impulse)
        val newPosition = entity.position + (velocity * delta) + vec2(0f, gravity) * (delta * delta / 2f)
        entity.velocity.y += gravity * delta

        decayImpulse(delta)

        val collider = colliderComponent.shape
        collider.setPosition(newPosition.x, entity.position.y)
        if (!collisionSystem.collidesWithTerrain(collider)) {
            entity.position.x = newPosition.x
        }
        collider.setPosition(entity.position.x, newPosition.y)
        if (collisionSystem.collidesWithTerrain(collider)) {
            isOnGround = entity.velocity.y < 0f
            entity.velocity.y = 0f
            impulse.y = 0f
        } else {
            isOnGround = false
            entity.position.y = newPosition.y
        }
        collider.setPosition(entity.position.x, entity.position.y)
    }

    fun applyImpulse(direction: Vector2, force: Float) {
        this.impulse += direction.cpy().setLength(force)
    }

   private val impulseDecayRate = 10f // higher = quicker decay
    private fun decayImpulse(delta: Float) {
        impulse.scl(exp(-impulseDecayRate * delta))
        if (impulse.len2() < 0.1f) {
            impulse.setZero()
        }
    }
}
