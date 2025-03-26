package dev.bakke.artofjuice

import com.badlogic.gdx.math.Rectangle

class World {
    val entities = mutableListOf<Entity>()
    val rects = mutableListOf<Rectangle>()

    fun addEntity(entity: Entity) {
        entities.add(entity)
        entity.world = this
    }

    fun update(delta: Float) {
        entities.forEach { it.update(delta) }
    }

    fun render() {
        entities.forEach { it.render() }
    }
}
