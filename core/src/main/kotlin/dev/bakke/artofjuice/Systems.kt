package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.engine.ParticleSystem
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.gun.GunVisualsManager
import ktx.assets.disposeSafely
import ktx.inject.Context

class Systems(private val context: Context) : Disposable {
    val assets = Assets().also(context::bindSingleton)
    val collisionSystem = CollisionSystem().also(context::bindSingleton)
    val particleSystem = ParticleSystem().also(context::bindSingleton)
    val screenshakeSystem = ScreenshakeSystem().also(context::bindSingleton)
    val shockwaveSystem = ShockwaveSystem().also(context::bindSingleton)
    val gunVisualsManager = GunVisualsManager(assets).also(context::bindSingleton)

    fun update(delta: Float) {
        collisionSystem.update(delta)
        screenshakeSystem.update(delta)
        shockwaveSystem.update(delta)
        particleSystem.update(delta)
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        collisionSystem.render(batch, shape)
        particleSystem.render(batch)
    }

    override fun dispose() {
        assets.dispose()
        particleSystem.disposeSafely()
        gunVisualsManager.disposeSafely()
    }
}
