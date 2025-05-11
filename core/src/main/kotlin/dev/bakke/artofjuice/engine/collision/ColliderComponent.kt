package dev.bakke.artofjuice.engine.collision

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.engine.Event.Event1
import dev.bakke.artofjuice.engine.collision.shapes.CollisionShape
import dev.bakke.artofjuice.engine.components.Component
import ktx.graphics.use

open class ColliderComponent(val shape: CollisionShape, val collidesWithTerrain: Boolean = false) : Component() {
    val onCollision = Event1<Entity>()
    var onTerrainCollision = Event1<CollisionShape>()

    fun disableEntityCollisions() {
        getSystem<CollisionSystem>().removeEntityCollider(this)
    }
    override fun init() {
        getSystem<CollisionSystem>().addEntityCollider(this)
        resetPosition()
    }

    override fun dispose() {
        getSystem<CollisionSystem>().removeEntityCollider(this)
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
