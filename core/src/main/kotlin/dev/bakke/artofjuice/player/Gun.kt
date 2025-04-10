package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.BulletComponent
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.SpriteComponent
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.vec2

enum class PlayerArms {
    One,
    Two,
}

data class GunStats(
    val damage: Int,
    val bulletSpeed: Float,
    val fireRate: Float,
    val gunSprite: Sprite,
    val bulletSprite: Sprite,
    val arms: PlayerArms,
) {
    companion object {
        val DEFAULT = GunStats(
            10,
            800f,
            0.05f,
            TextureAtlas("Weapons.atlas").findRegion("pistol1").let(::Sprite),
            TextureAtlas("Weapons.atlas").findRegion("pistol_bullet1").let(::Sprite),
            PlayerArms.One
        )
        val SNIPER = GunStats(
            100,
            1200f,
            0.4f,
            TextureAtlas("Weapons.atlas").findRegion("rifle6").let(::Sprite),
            TextureAtlas("Weapons.atlas").findRegion("rifle_bullet6").let(::Sprite),
            PlayerArms.Two
        )
    }
}

class GunComponent(var stats: GunStats?) : Component() {
    private var timeSinceLastShot = stats?.fireRate ?: 0f
    private val playerAtlas = TextureAtlas("new_character.atlas")

    override fun update(delta: Float) {
        timeSinceLastShot = (timeSinceLastShot + delta).coerceAtMost(stats?.fireRate ?: 0f)
    }

    fun shoot(position: Vector2, direction: Vector2) {
        if (stats == null) return
        val stats = this.stats!!
        if (timeSinceLastShot < stats.fireRate) return
        timeSinceLastShot %= stats.fireRate
        entity.world.entity(entity.position.cpy()) {
            velocity = direction.cpy().nor().scl(stats.bulletSpeed)
            +Tag.PROJECTILE
            +BulletComponent(stats)
            +SpriteComponent(stats.bulletSprite)
            +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 12f, 4f)), true)
        }
    }

    private fun setGunStats(stats: GunStats) {
        this.stats = stats
        this.timeSinceLastShot = stats.fireRate
    }

    override fun dispose() {
        playerAtlas.disposeSafely()
    }
}
