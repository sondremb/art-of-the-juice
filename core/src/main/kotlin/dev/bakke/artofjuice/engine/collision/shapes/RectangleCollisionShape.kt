package dev.bakke.artofjuice.engine.collision.shapes

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.engine.gdx.extensions.rect

class RectangleCollisionShape(val rect: Rectangle) : CollisionShape {
    override fun collidesWith(other: CollisionShape): Boolean {
        return other.collidesWithRect(this)
    }

    override fun collidesWithRect(other: RectangleCollisionShape): Boolean {
        return rect.overlaps(other.rect)
    }

    override fun collidesWithCircle(other: CircleCollisionShape): Boolean {
        val closestX = other.circle.x.coerceIn(rect.x, rect.x + rect.width)
        val closestY = other.circle.y.coerceIn(rect.y, rect.y + rect.height)
        return other.circle.contains(closestX, closestY)
    }

    override fun collidesWithPoint(other: PointCollisionShape): Boolean {
        return rect.contains(other.point)
    }

    override fun setPosition(x: Float, y: Float) {
        this.rect.setCenter(x, y)
    }

    override fun renderDebug(shape: ShapeRenderer) {
        shape.rect(rect)
    }
}
