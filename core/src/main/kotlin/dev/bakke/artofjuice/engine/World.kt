package dev.bakke.artofjuice.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import ktx.inject.Context

class World(val context: Context) {
    val entities = mutableListOf<Entity>()
    private val nextEntities = mutableListOf<Entity>()
    private val entitiesToRemove = mutableListOf<Entity>()

    fun spawnEntity(position: Vector2, block: Entity.() -> Unit): Entity {
        val entity = Entity(this, position)
        entity.block()
        nextEntities.add(entity)
        return entity
    }

    fun destroyEntity(entity: Entity) {
        entitiesToRemove.add(entity)
    }

    fun update(delta: Float) {
        addNewEntities()
        removeDestroyedEntities()
        entities.forEach { it.update(delta) }
    }

    private fun addNewEntities() {
        nextEntities.forEach { it.init() }
        entities.addAll(nextEntities)
        nextEntities.clear()
    }

    private fun removeDestroyedEntities() {
        entitiesToRemove.forEach { it.dispose() }
        entities.removeAll(entitiesToRemove)
    }

    fun render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        entities.forEach { it.render(spriteBatch, shapeRenderer) }
    }
}
