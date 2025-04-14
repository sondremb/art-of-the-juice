package dev.bakke.artofjuice.engine.collision.shapes

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

interface CollisionShape {
    fun collidesWith(other: CollisionShape): Boolean
    fun collidesWithRect(other: RectangleCollisionShape): Boolean
    fun collidesWithCircle(other: CircleCollisionShape): Boolean
    fun collidesWithPoint(other: PointCollisionShape): Boolean

    fun setPosition(x: Float, y: Float)
    fun setPosition(position: Vector2) {
        setPosition(position.x, position.y)
    }

    fun renderDebug(shape: ShapeRenderer)
}

