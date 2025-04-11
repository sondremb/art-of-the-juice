package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.BulletComponent
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.components.SpriteComponent
import ktx.assets.disposeSafely
import ktx.math.unaryMinus

enum class PlayerArms {
    One,
    Two,
}

class GunComponent(var stats: GunStats?) : Component() {
    private var timeSinceLastShot = stats?.fireRate ?: 0f
    private val playerAtlas = TextureAtlas("new_character.atlas")

    private lateinit var physicsComponent: PhysicsComponent
    private lateinit var screenshakeSystem: ScreenshakeSystem
    override fun lateInit() {
        physicsComponent = getComponent()
        screenshakeSystem = context.inject()
    }

    override fun update(delta: Float) {
        timeSinceLastShot = (timeSinceLastShot + delta).coerceAtMost(stats?.fireRate ?: 0f)
    }

    fun shoot(position: Vector2, direction: Vector2) {
        if (stats == null) return
        val stats = this.stats!!
        if (timeSinceLastShot < stats.fireRate) return
        timeSinceLastShot %= stats.fireRate
        physicsComponent.applyImpulse(
            // TODO player knockback som egen stat?
            -direction, stats.impulse
        )
        screenshakeSystem.setMin(stats.shakeIntensity)
        entity.world.entity(position.cpy()) {
            velocity = direction.cpy().setLength(stats.bulletSpeed)
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
