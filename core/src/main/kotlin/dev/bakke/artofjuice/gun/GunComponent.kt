package dev.bakke.artofjuice.gun

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.engine.AnimationRenderable
import dev.bakke.artofjuice.engine.ParticleSystem
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import dev.bakke.artofjuice.engine.components.SpriteComponent
import dev.bakke.artofjuice.engine.gdx.extensions.rect
import ktx.assets.toInternalFile
import ktx.graphics.use
import ktx.math.plus
import ktx.math.unaryMinus



class GunComponent(initialGun: Gun?) : Component() {
    private var timeSinceLastShot = 0f

    var gun: Gun? = initialGun
        set(value) {
            field = value
            value?.let {
                timeSinceLastShot = it.stats.fireRate
            }
        }

    private  var physicsComponent: PhysicsComponent? = null
    private lateinit var screenshakeSystem: ScreenshakeSystem
    private lateinit var particleSystem: ParticleSystem
    override fun lateInit() {
        physicsComponent = tryGetComponent()
        screenshakeSystem = context.inject()
        particleSystem = context.inject()
        gun?.let { g ->
            timeSinceLastShot = g.stats.fireRate
        }
    }

    override fun update(delta: Float) {
        timeSinceLastShot = (timeSinceLastShot + delta).coerceAtMost(gun?.stats?.fireRate ?: 0f)
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (gun == null) return
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                val rect = Rectangle(0f, 0f, 2f, 2f)
                // TODO flip these somehow
                rect.setCenter(
                    entity.position.x + gun!!.visuals.gunOffset.x,
                    entity.position.y + gun!!.visuals.gunOffset.y
                )
                shape.rect(rect)
                rect.x += gun!!.visuals.bulletOffset.x
                rect.y += gun!!.visuals.bulletOffset.y
                shape.rect(rect)
            }
        }
    }

    fun shoot(direction: Vector2) {
        if (gun == null) return
        val gun = this.gun!!
        if (timeSinceLastShot < gun.stats.fireRate) return
        timeSinceLastShot %= gun.stats.fireRate
        physicsComponent?.applyImpulse(
            // TODO player knockback som egen stat?
            -direction, gun.stats.impulse
        )
        screenshakeSystem.setMin(gun.stats.shakeIntensity)
        val offsetScaleX = if (direction.x < 0) -1f else 1f
        val offset = (gun.visuals.gunOffset + gun.visuals.bulletOffset).scl(offsetScaleX, 1f)
        val animation = TextureAtlas("Effects.atlas".toInternalFile())
            .findRegions("effect8")
            .let { Animation(1/24f, it) }
        particleSystem.spawn(
            AnimationRenderable(animation),
            entity.position + offset,
            Vector2.Zero.cpy(),
            0.05f
        )
        spawnEntity(entity.position + offset) {
            velocity = direction.cpy().setLength(gun.stats.bulletSpeed)
            +Tag.PROJECTILE
            +BulletComponent(gun.stats)
            +SpriteComponent(gun.bulletSprite)
            +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 12f, 4f)), true)
        }
    }
}
