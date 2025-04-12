package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.BulletComponent
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.components.SpriteComponent
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.plus
import ktx.math.unaryMinus



class GunComponent(initialStats: GunStats?) : Component() {
    private var timeSinceLastShot = 0f
    private val weaponsAtlas = TextureAtlas("weapons.atlas")
    var gunSprite: Sprite? = null
        private set
    private var bulletSprite: Sprite? = null

    var stats: GunStats? = initialStats
        set(value) {
            field = value
            value?.let {
                timeSinceLastShot = it.fireRate
                updateVisuals()
            }
        }

    private  var physicsComponent: PhysicsComponent? = null
    private lateinit var screenshakeSystem: ScreenshakeSystem
    override fun lateInit() {
        physicsComponent = tryGetComponent()
        screenshakeSystem = context.inject()
        stats?.let { stats ->
            timeSinceLastShot = stats.fireRate
            updateVisuals()
        }
    }

    fun updateVisuals() {
        stats?.let {
            bulletSprite = Sprite(weaponsAtlas.findRegion(it.bulletSprite))
            gunSprite = Sprite(weaponsAtlas.findRegion(it.visuals.spriteName))
        }
    }

    override fun update(delta: Float) {
        timeSinceLastShot = (timeSinceLastShot + delta).coerceAtMost(stats?.fireRate ?: 0f)
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (stats == null) return
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                val rect = Rectangle(0f, 0f, 2f, 2f)
                // TODO flip these somehow
                rect.setCenter(
                    entity.position.x + stats!!.visuals.gunOffset.x,
                    entity.position.y + stats!!.visuals.gunOffset.y
                )
                shape.rect(rect)
                rect.x += stats!!.visuals.bulletOffset.x
                rect.y += stats!!.visuals.bulletOffset.y
                shape.rect(rect)
            }
        }
    }

    fun shoot(direction: Vector2) {
        if (stats == null) return
        val stats = this.stats!!
        if (timeSinceLastShot < stats.fireRate) return
        timeSinceLastShot %= stats.fireRate
        physicsComponent?.applyImpulse(
            // TODO player knockback som egen stat?
            -direction, stats.impulse
        )
        screenshakeSystem.setMin(stats.shakeIntensity)
        val offsetScaleX = if (direction.x < 0) -1f else 1f
        val offset = (stats.visuals.gunOffset + stats.visuals.bulletOffset).scl(offsetScaleX, 1f)
        spawnEntity(entity.position + offset) {
            velocity = direction.cpy().setLength(stats.bulletSpeed)
            +Tag.PROJECTILE
            +BulletComponent(stats)
            +SpriteComponent(bulletSprite!!)
            +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 12f, 4f)), true)
        }
    }

    override fun dispose() {
        weaponsAtlas.disposeSafely()
    }
}
