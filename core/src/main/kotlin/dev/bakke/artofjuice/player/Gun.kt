package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.BulletComponent
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.SpriteComponent

data class GunStats(val damage: Int, val bulletSpeed: Float, val fireRate: Float, val sprite: Sprite) {
    companion object {
        val DEFAULT = GunStats(
            30,
            800f,
            0.4f,
            TextureAtlas("Bullets.atlas").findRegion("2").let(::Sprite))
    }
}

class GunComponent(private val stats: GunStats) : Component() {
    private var timeSinceLastShot = stats.fireRate

    override fun update(delta: Float) {
        timeSinceLastShot += delta
    }

    fun shoot(position: Vector2, direction: Vector2) {
        if (timeSinceLastShot < stats.fireRate) return
        timeSinceLastShot = 0f
        entity.world.entity(entity.position.cpy()) {
            velocity = direction.cpy().nor().scl(stats.bulletSpeed)
            +Tag.PROJECTILE
            +BulletComponent(stats)
            +SpriteComponent(stats.sprite)
            +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 12f, 4f)), true)
        }
    }
}
