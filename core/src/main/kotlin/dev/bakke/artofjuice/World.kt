package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class World {
    val entities = mutableListOf<Entity>()
    val rects = mutableListOf<Rectangle>()
    private val nextEntities = mutableListOf<Entity>()

    fun addEntity(entity: Entity) {
        nextEntities.add(entity)
        entity.world = this
    }

    fun update(delta: Float) {
        entities.addAll(nextEntities)
        nextEntities.clear()
        entities.forEach { it.update(delta) }
    }

    fun render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        entities.forEach { it.render(spriteBatch, shapeRenderer) }
    }
}
