package dev.bakke.artofjuice.gun

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.Assets
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.TextureAssets
import dev.bakke.artofjuice.engine.AnimationRenderable
import dev.bakke.artofjuice.engine.ParticleSystem
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.SpriteComponent
import dev.bakke.artofjuice.engine.gdx.extensions.rect
import ktx.graphics.use
import ktx.math.plus

class GunComponent(initialGun: Gun?) : Component() {
    private var timeSinceLastShot = 0f

    var gun: Gun? = initialGun
        set(value) {
            field = value
            value?.let {
                timeSinceLastShot = it.stats.fireRate
            }
        }

    private val particleSystem: ParticleSystem by getSystemLazy()
    // 💡HINT: sånn får du tak i et system
    private val assets: Assets by getSystemLazy()
    override fun lateInit() {
        gun?.let { g ->
            timeSinceLastShot = g.stats.fireRate
        }
    }

    override fun update(delta: Float) {
        timeSinceLastShot += delta
    }

    fun shoot(direction: Vector2) {
        if (gun == null) return
        val gun = this.gun!!
        if (timeSinceLastShot < gun.stats.fireRate)  {
            return
        }
        timeSinceLastShot = 0f

        // OPPGAVE 3:

        // OPPGAVE 4:

        val bulletPosition = getBulletPosition(direction)
        spawnBullet(bulletPosition, direction)
        createMuzzleFlash(bulletPosition)
    }

    private fun getBulletPosition(direction: Vector2): Vector2 {
        val offsetScaleX = if (direction.x < 0) -1f else 1f
        val offset = (gun!!.visuals.gunOffset + gun!!.visuals.bulletOffset).scl(offsetScaleX, 1f)
        return entity.position + offset
    }

    private fun createMuzzleFlash(position: Vector2) {
        val animation = assets.getRegions(TextureAssets.Effects.Effect8)
            .let { Animation(1/24f, it) }
        particleSystem.spawn(
            AnimationRenderable(animation),
            position,
            Vector2.Zero.cpy(),
            0.05f
        )
    }

    private fun spawnBullet(position: Vector2, direction: Vector2) {
        val gun = this.gun!!
        spawnEntity(position) {
            velocity = direction.cpy().setLength(gun.stats.bulletSpeed)
            +Tag.PROJECTILE
            +BulletComponent(gun.stats)
            +SpriteComponent(gun.bulletSprite)
            +ColliderComponent(RectangleCollisionShape( 12f, 4f), true)
        }
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
}
