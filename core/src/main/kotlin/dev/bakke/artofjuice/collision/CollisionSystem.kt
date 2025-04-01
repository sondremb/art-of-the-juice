package dev.bakke.artofjuice.collision

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.collision.shapes.CollisionShape
import ktx.graphics.use

class CollisionSystem() {
    private val terrainColliders = mutableListOf<CollisionShape>()
    private val entityColliders = mutableListOf<ColliderComponent>()

    fun update(delta: Float) {
        for (i in 0 until entityColliders.size - 1) {
            val collider = entityColliders[i]
            if (!collider.isActive) continue
            for (j in i + 1 until entityColliders.size) {
                val other = entityColliders[j]
                if (!other.isActive) continue
                if (collider.collidesWith(other)) {
                    collider.onCollision?.invoke(other.entity)
                    other.onCollision?.invoke(collider.entity)
                }
            }
        }
    }
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
        return entityColliders.any { it.collidesWith(collider) }
    }
    fun collidesWithEntities(collider: CollisionShape): Boolean {
        return entityColliders.any { it.collidesWith(collider) }
    }
    fun getEntityCollisions(collider: CollisionShape): List<ColliderComponent> {
        return entityColliders.filter { it.collidesWith(collider) }
    }
    fun getEntityCollisions(collider: ColliderComponent): List<ColliderComponent> {
        return entityColliders.filter { it.collidesWith(collider) }
    }
}
