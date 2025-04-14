package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.collision.shapes.CircleCollisionShape
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import dev.bakke.artofjuice.engine.components.SpriteComponent
import ktx.assets.toInternalFile

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
