package dev.bakke.artofjuice

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.enemy.SkaterAnimatedSprite
import ktx.math.vec2

class SpawnEnemyComponent(private var timeBetween: Float) : Component() {
    private var timeSinceLastSpawn = timeBetween
    override fun update(delta: Float) {
        timeSinceLastSpawn += delta
        if (timeSinceLastSpawn >= timeBetween) {
            timeSinceLastSpawn %= timeBetween
            entity.world.spawnEnemy(entity.position.cpy(), Math.random().toFloat() - 0.5f)
        }
    }
}

fun World.spawnEnemy(position: Vector2, direction: Float) {
    spawnEntity(position) {
        +Tag.ENEMY
        +EnemyAIComponent(direction)
        +PhysicsComponent(-900f)
        +SkaterAnimatedSprite()
        +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 24f, 32f)))
        +HealthComponent(100)
        +HealthBarComponent(vec2(0f, 20f), 32f, 4f)
    }
}
