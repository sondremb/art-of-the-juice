package dev.bakke.artofjuice

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.enemy.SkaterAnimatedSprite

class SpawnEnemyComponent(private var timeBetween: Float) : Component() {
    private var timeSinceLastSpawn = timeBetween
    override fun update(delta: Float) {
        timeSinceLastSpawn += delta
        if (timeSinceLastSpawn >= timeBetween) {
            timeSinceLastSpawn %= timeBetween
            entity.world.spawnEnemy(entity.position.cpy())
        }
    }
}

fun World.spawnEnemy(position: Vector2) {
    entity(position) {
        +Tag.ENEMY
        +EnemyAIComponent()
        +PhysicsComponent(-900f)
        +SkaterAnimatedSprite()
        +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 24f, 32f)))
    }
}
