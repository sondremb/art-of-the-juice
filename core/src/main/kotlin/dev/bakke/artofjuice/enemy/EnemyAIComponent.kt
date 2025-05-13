package dev.bakke.artofjuice.enemy

import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import ktx.math.vec2
import kotlin.math.sign

class EnemyAIComponent(private var direction: Float = 1f, private var speed: Float) : Component() {
    private val animatedSprite: EnemyAnimatedSprite by getComponentLazy()
    private val colliderComponent: ColliderComponent by getComponentLazy()
    private val collisionSystem: CollisionSystem by getSystemLazy()

    override fun lateInit() {
        entity.velocity.x = speed * sign(direction)
        val healthComponent = getComponent<HealthComponent>()
        healthComponent.onDamage += ::onHit
        healthComponent.onDeath += ::onDeath
    }

    private fun onHit(damage: Int) {
        // OPPGAVE 1
        // hva bør skje her? 🤔
    }

    private fun onDeath() {
        // OPPGAVE 6

        // fjerner enemy fra verden
        entity.destroy()
    }

    override fun update(delta: Float) {
        if (!isActive) return

        if (willCollideWithTerrain(delta)) {
            entity.velocity.x = -entity.velocity.x
        }
        animatedSprite.flipX = entity.velocity.x < 0

        // 💡HINT: sånn setter man animasjon!
        animatedSprite.requestTransition(EnemyAnimatedSprite.State.RUN)
    }

    private fun willCollideWithTerrain(delta: Float): Boolean {
        val nextPosition = vec2(entity.position.x + entity.velocity.x * delta, entity.position.y)
        colliderComponent.shape.setPosition(nextPosition.x, nextPosition.y)
        val willCollide = collisionSystem.collidesWithTerrain(colliderComponent.shape)
        colliderComponent.resetPosition()

        return willCollide
    }
}
