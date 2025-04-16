package dev.bakke.artofjuice.engine.collision.shapes

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle

class CircleCollisionShape(val circle: Circle) : CollisionShape {
    constructor(radius: Float) : this(Circle(0f, 0f, radius))
    override fun collidesWith(other: CollisionShape): Boolean {
        return other.collidesWithCircle(this)
    }

    override fun collidesWithRect(other: RectangleCollisionShape): Boolean {
        val closestX = circle.x.coerceIn(other.rect.x, other.rect.x + other.rect.width)
        val closestY = circle.y.coerceIn(other.rect.y, other.rect.y + other.rect.height)
        return circle.contains(closestX, closestY)
    }

    override fun collidesWithCircle(other: CircleCollisionShape): Boolean {
        return circle.overlaps(other.circle)
    }

    override fun collidesWithPoint(other: PointCollisionShape): Boolean {
        return circle.contains(other.point)
    }

    override fun setPosition(x: Float, y: Float) {
        this.circle.setPosition(x, y)
    }

    override fun renderDebug(shape: ShapeRenderer) {
        shape.circle(circle.x, circle.y, circle.radius)
    }
}
