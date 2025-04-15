package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.engine.collision.shapes.CircleCollisionShape
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import ktx.graphics.use

class ExplosionComponent(
    private var explosionRadius: Float,
    private var damage: Int,
    private var knockbackIntensity: Float = 1000f,
    private var screenshakeIntensity: Float = 0.8f,
    private var lingerTime: Float = 0.1f
) : Component() {

    private var hasExploded = false
    private var timeSinceExplosion = 0f

    override fun update(delta: Float) {
        if (!hasExploded) {
            explode()
            return
        }
        timeSinceExplosion += delta
        if (timeSinceExplosion >= lingerTime) {
            entity.destroy()
        }
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        shape.use(ShapeRenderer.ShapeType.Filled) {
            it.color = Color.WHITE
            shape.circle(entity.position.x, entity.position.y, explosionRadius)
        }
    }

    private fun explode() {
        hasExploded = true
        val collisionSystem = context.inject<CollisionSystem>()
        collisionSystem.getEntityCollisions(CircleCollisionShape(Circle(entity.position.cpy(), explosionRadius)))
            .forEach {
                if (it.entity.hasTag(Tag.ENEMY)) {
                    it.getComponent<HealthComponent>().damage(damage)
                    it.getComponent<PhysicsComponent>().applyImpulse(
                        Vector2(it.entity.position.cpy().sub(entity.position.cpy().sub(0f, 32f))), knockbackIntensity
                    )
                }
            }
        context.inject<ScreenshakeSystem>().shake(screenshakeIntensity)
    }
}
