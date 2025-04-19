package dev.bakke.artofjuice.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.utils.DeferredList
import ktx.inject.Context

class World(val context: Context) {
    val entities = DeferredList<Entity>()

    fun spawnEntity(position: Vector2, block: Entity.() -> Unit): Entity {
        val entity = Entity(this, position)
        entity.block()
        entities.add(entity)
        return entity
    }

    fun destroyEntity(entity: Entity) {
        entities.remove(entity)
    }

    fun update(delta: Float) {
        entities.update(
            beforeAdd = { it.init() },
            beforeRemove = { it.dispose() }
        )
        entities.items.forEach { it.update(delta) }
    }

    fun render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        entities.items.forEach { it.render(spriteBatch, shapeRenderer) }
    }
}
