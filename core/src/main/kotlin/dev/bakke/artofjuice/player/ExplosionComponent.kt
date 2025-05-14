package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.ShockwaveSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.engine.collision.shapes.CircleCollisionShape
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import ktx.graphics.use
import ktx.math.minus

class ExplosionComponent(
    private var explosionRadius: Float,
    private var damage: Int,
    private var knockbackIntensity: Float = 1000f,
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
        val collisionSystem = getSystem<CollisionSystem>()
        collisionSystem
            .getEntityCollisions(CircleCollisionShape(Circle(entity.position.cpy(), explosionRadius)))
            .forEach { applyExplossionToEntity(it.entity) }
        val shockwaveSystem = getSystem<ShockwaveSystem>()
        shockwaveSystem.addShockwave(entity.position.cpy())

        // OPPGAVE 3C
        // få tak i ScreenshakeSystem, og kall .addShake() eller .setMinimumShake()
        getSystem<ScreenshakeSystem>().addScreenShake(0.5f)
    }

    private fun applyExplossionToEntity(other: Entity) {
        if (other.hasTag(Tag.ENEMY)) {
            other.getComponent<HealthComponent>().damage(damage)
        }
        // jukser og later som om eksplosjonen er lavere, for å få mer oppover-retning på knockback
        val belowExplosion = entity.position.cpy().sub(0f, explosionRadius)
        val directionFromExplosion = other.position - belowExplosion
        other.tryGetComponent<PhysicsComponent>()?.applyImpulse(
            directionFromExplosion,
            knockbackIntensity
        )
    }
}
