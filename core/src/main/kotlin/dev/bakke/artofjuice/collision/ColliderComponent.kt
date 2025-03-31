package dev.bakke.artofjuice.collision

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.Entity
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.collision.shapes.CollisionShape
import dev.bakke.artofjuice.components.Component
import ktx.graphics.use

open class ColliderComponent(val shape: CollisionShape) : Component() {
    var onCollision: ((Entity) -> Unit)? = null

    fun onCollision(callback: (Entity) -> Unit) {
        this.onCollision = callback
    }

    override fun init() {
        context.inject<CollisionSystem>().addEntityCollider(this)
        resetPosition()
    }

    override fun dispose() {
        context.inject<CollisionSystem>().removeEntityCollider(this)
    }

    override fun update(delta: Float) {
        resetPosition()
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                this.shape.renderDebug(shape)
            }
        }
    }

    fun collidesWith(other: CollisionShape): Boolean {
        return shape.collidesWith(other)
    }
    fun collidesWith(other: ColliderComponent): Boolean {
        return shape.collidesWith(other.shape)
    }

    fun setPosition(x: Float, y: Float) {
        shape.setPosition(x, y)
    }
    fun setPosition(position: Vector2) {
        shape.setPosition(position)
    }
    fun resetPosition() {
        shape.setPosition(entity.position)
    }
}
