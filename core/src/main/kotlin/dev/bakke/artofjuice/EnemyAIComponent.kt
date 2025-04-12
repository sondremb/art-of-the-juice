package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Circle
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.enemy.SkaterAnimatedSprite
import dev.bakke.artofjuice.collision.CollisionSystem
import dev.bakke.artofjuice.collision.shapes.CircleCollisionShape
import dev.bakke.artofjuice.components.SpriteComponent
import dev.bakke.artofjuice.player.GrenadeComponent
import ktx.assets.toInternalFile
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
        getComponent<HealthComponent>().onDeath {
            if (Math.random() < 0.2f) {
                entity.world.spawnEntity(entity.position.cpy()) {
                    // TODO refactorer til å separere granat og eksplosjon
                    position = entity.position.cpy()
                    +SpriteComponent(Sprite(Texture("grenade.png".toInternalFile())))
                    +ColliderComponent(CircleCollisionShape(Circle(entity.position.cpy(), 8f)))
                    +GrenadeComponent(0f, 50f, 70)
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
