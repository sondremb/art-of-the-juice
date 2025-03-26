package dev.bakke.artofjuice

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class EnemyAIComponent {
    private val speed = 100f // Horizontal speed

    fun update(entity: Entity, delta: Float) {
        if (entity.velocity.x == 0f) {
            entity.velocity.x = speed
        }
        if (entity.collider == null) {
            return
        }
        val collider = entity.collider!!
        val nextPosition = vec2(entity.position.x + entity.velocity.x * delta, entity.position.y)
        if (collidesWithMap(entity.world.rects, collider, nextPosition)) {
            entity.velocity.x = -entity.velocity.x
        }
    }

    private fun collidesWithMap(map: Collection<Rectangle>, collider: Rectangle, newPosition: Vector2): Boolean {
        val box = Rectangle(newPosition.x, newPosition.y, collider.width, collider.height)
        return map.any { box.overlaps(it) }
    }
}
