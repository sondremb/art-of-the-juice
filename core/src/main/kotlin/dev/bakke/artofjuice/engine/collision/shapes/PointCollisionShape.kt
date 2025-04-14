package dev.bakke.artofjuice.engine.collision.shapes

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class PointCollisionShape(val point: Vector2) : CollisionShape {
    override fun collidesWith(other: CollisionShape): Boolean {
        return other.collidesWithPoint(this)
    }

    override fun collidesWithRect(other: RectangleCollisionShape): Boolean {
        return other.rect.contains(point)
    }

    override fun collidesWithCircle(other: CircleCollisionShape): Boolean {
        return other.circle.contains(point)
    }

    override fun collidesWithPoint(other: PointCollisionShape): Boolean {
        return other.point == point
    }

    override fun setPosition(x: Float, y: Float) {
        this.point.set(x, y)
    }

    override fun renderDebug(shape: ShapeRenderer) {
        shape.circle(point.x, point.y, 1f)
    }
}
