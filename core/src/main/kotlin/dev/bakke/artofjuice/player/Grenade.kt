package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.CollisionSystem
import dev.bakke.artofjuice.collision.shapes.CircleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.components.SpriteComponent
import ktx.assets.toInternalFile
import ktx.graphics.use

class GrenadeComponent(var fuseTime: Float, var explosionRadius: Float, var damage: Int) : Component() {
    private var timeSinceThrown = 0f
    private var hasExploded = false
    private var timeSinceExplosion = 0f

    override fun update(delta: Float) {
        if (hasExploded) {
            timeSinceExplosion += delta
            if (timeSinceExplosion >= 0.1f) {
                entity.destroy()
            }
        } else {
            timeSinceThrown += delta
            if (timeSinceThrown >= fuseTime) {
                explode()
            }
        }
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (hasExploded) {
            shape.use(ShapeRenderer.ShapeType.Filled) {
                it.color = Color.ORANGE
                shape.circle(entity.position.x, entity.position.y, explosionRadius)
            }
        }
    }

    private fun explode() {
        hasExploded = true
        entity.velocity = Vector2.Zero
        tryGetComponent<PhysicsComponent>()?.gravity = 0f
        val collisionSystem = context.inject<CollisionSystem>()
        getComponent<SpriteComponent>().isActive = false
        collisionSystem.getEntityCollisions(CircleCollisionShape(Circle(entity.position.cpy(), explosionRadius))).forEach {
            if (it.entity.hasTag(Tag.ENEMY)) {
                it.getComponent<HealthComponent>().damage(damage)
                it.getComponent<PhysicsComponent>().applyImpulse(
                    Vector2(it.entity.position.cpy().sub(entity.position.cpy().sub(0f, 32f))), 2000f
                )
            }
        }
        context.inject<ScreenshakeSystem>().shake(1f)
    }
}

class GrenadeThrowerComponent : Component() {
    private val throwCooldown = 1f
    private var timeSinceThrow = throwCooldown

    override fun update(delta: Float) {
        timeSinceThrow += delta
    }

    fun throwGrenade(direction: Vector2) {
        if (timeSinceThrow < throwCooldown) return
        timeSinceThrow = 0f
        spawnEntity(entity.position.cpy()) {
            position = entity.position.cpy()
            velocity = direction.cpy().setLength(400f)
            +SpriteComponent(Sprite(Texture("grenade.png".toInternalFile())))
            +PhysicsComponent(-900f)
            +ColliderComponent(CircleCollisionShape(Circle(entity.position.cpy(), 8f)))
            +GrenadeComponent(1f, 100f, 70)
        }
    }
}
