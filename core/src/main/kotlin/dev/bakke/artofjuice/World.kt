package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class World {
    val entities = mutableListOf<Entity>()
    val rects = mutableListOf<Rectangle>()
    private val nextEntities = mutableListOf<Entity>()

    fun entity(position: Vector2, block: Entity.() -> Unit): Entity {
        val entity = Entity(this, position)
        entity.block()
        nextEntities.add(entity)
        return entity
    }

    fun update(delta: Float) {
        nextEntities.forEach { it.init() }
        entities.addAll(nextEntities)
        nextEntities.clear()
        entities.forEach { it.update(delta) }
    }

    fun render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        entities.forEach { it.render(spriteBatch, shapeRenderer) }
    }
}
