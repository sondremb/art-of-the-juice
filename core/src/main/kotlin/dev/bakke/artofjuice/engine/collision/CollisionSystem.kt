package dev.bakke.artofjuice.engine.collision

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.engine.collision.shapes.CollisionShape
import dev.bakke.artofjuice.engine.utils.DeferredList
import ktx.graphics.use

class CollisionSystem() {
    private val terrainColliders = mutableListOf<CollisionShape>()
    private val entityColliders = DeferredList<ColliderComponent>()

    fun update(delta: Float) {
        entityColliders.update()
        val entities = entityColliders.items
        if (entities.isEmpty()) return
        for (i in 0 until entities.size - 1) {
            val collider = entities[i]
            if (!collider.isActive) continue
            for (j in i + 1 until entities.size) {
                val other = entities[j]
                if (!other.isActive) continue
                if (collider.collidesWith(other)) {
                    collider.onCollision?.invoke(other.entity)
                    other.onCollision?.invoke(collider.entity)
                }
            }
            checkTerrainCollisions(collider)
        }
        checkTerrainCollisions(entities.last())
    }

    private fun checkTerrainCollisions(collider: ColliderComponent) {
        if (collider.collidesWithTerrain) {
            terrainColliders.forEach {
                if (collider.collidesWith(it)) {
                    collider.onTerrainCollision?.invoke(it)
                }
            }
        }
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                terrainColliders.forEach {
                    it.renderDebug(shape)
                }
            }
        }
    }

    fun addTerrainCollider(collider: CollisionShape) {
        terrainColliders.add(collider)
    }
    fun removeTerrainCollider(collider: CollisionShape) {
        terrainColliders.remove(collider)
    }
    fun addEntityCollider(collider: ColliderComponent) {
        entityColliders.add(collider)
    }
    fun removeEntityCollider(collider: ColliderComponent) {
        entityColliders.remove(collider)
    }

    fun collidesWithTerrain(collider: CollisionShape): Boolean {
        return terrainColliders.any { collider.collidesWith(it) }
    }
    fun collidesWithTerrain(collider: ColliderComponent): Boolean {
        return terrainColliders.any { collider.collidesWith(it) }
    }
    fun getTerrainCollisions(collider: CollisionShape): List<CollisionShape> {
        return terrainColliders.filter { collider.collidesWith(it) }
    }
    fun getTerrainCollisions(collider: ColliderComponent): List<CollisionShape> {
        return terrainColliders.filter { collider.collidesWith(it) }
    }
    fun collidesWithEntities(collider: ColliderComponent): Boolean {
        return entityColliders.items.any { it.collidesWith(collider) }
    }
    fun collidesWithEntities(collider: CollisionShape): Boolean {
        return entityColliders.items.any { it.collidesWith(collider) }
    }
    fun getEntityCollisions(collider: CollisionShape): List<ColliderComponent> {
        return entityColliders.items.filter { it.collidesWith(collider) }
    }
    fun getEntityCollisions(collider: ColliderComponent): List<ColliderComponent> {
        return entityColliders.items.filter { it.collidesWith(collider) }
    }
}
