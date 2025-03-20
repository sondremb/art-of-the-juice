package dev.bakke.artofjuice.components

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.Entity
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2

class PhysicsComponent(var gravity: Float) {
    var isOnGround = false
        private set
    fun update(entity: Entity, delta: Float, rects: Collection<Rectangle>) {

        // Apply gravity
        val newPosition = entity.position + (entity.velocity * delta) + vec2(0f, gravity) * (delta * delta / 2f)
        entity.velocity.y += gravity * delta

        if (entity.collider == null) {
            entity.position = newPosition
            return
        }

        val collider = entity.collider!!
        if (!collidesWithMap(rects, collider, vec2(newPosition.x, entity.position.y))) {
            entity.position.x = newPosition.x
        }
        if (collidesWithMap(rects, collider, vec2(entity.position.x, newPosition.y))) {
            isOnGround = entity.velocity.y < 0f
            entity.velocity.y = 0f
        } else {
            isOnGround = false
            entity.position.y = newPosition.y
        }
        entity.collider!!.setPosition(entity.position)
    }

    fun collidesWithMap(map: Collection<Rectangle>, collider: Rectangle, newPosition: Vector2): Boolean {
        val box = Rectangle(newPosition.x, newPosition.y, collider.width, collider.height)
        return map.any { box.overlaps(it) }
    }
}
