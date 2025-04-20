package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.utils.Perlin
import ktx.graphics.use
import ktx.math.unaryMinus

// "trauma"-basert screenshake med Perlin noise, basert på denne GDC-talken:
// "Math for Game Programmers: Juicing Your Cameras With Math" av Squirrel Eiserloh
// https://www.youtube.com/watch?v=tu-Qe66AvtY
class ScreenshakeSystem(private val camera: Camera, private val player: Entity) {
    private var trauma = 0f
    // trauma lost per second
    private var traumaDecay = 0.8f
    private var maxAmplitude = 50f
    private var frequency = 10f
    private var maxRotationDegrees = 5f
    private var seed = 0.2387f; // guaranteed random!
    private var time = 0f

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (GamePreferences.renderDebug()) {
            val w = Gdx.graphics.width.toFloat() - 20f
            shape.use(ShapeRenderer.ShapeType.Filled) {
                shape.color = Color.RED
                shape.rect(10f, 10f, w * trauma, 10f)
                shape.color = Color.ORANGE
                shape.rect(10f, 20f, w * trauma * trauma, 10f)
            }
            shape.use(ShapeRenderer.ShapeType.Line) {
                shape.color = Color.WHITE
                shape.rect(10f, 10f, w, 20f)
            }
        }
    }

    fun update(delta: Float) {
        time += delta
        resetRotation()
        if (trauma > 0f) {
            trauma = (trauma - delta * traumaDecay).coerceAtLeast(0f)
            val t2 = trauma * trauma * trauma
            val amplitude = maxAmplitude * t2
            val offsetX = noise(seed) * amplitude
            val offsetY = noise(seed + 1) * amplitude
            val rotation = noise(seed + 2) * maxRotationDegrees * t2
            camera.position.x += offsetX
            camera.position.y += offsetY
            camera.rotate(Vector3.Z, rotation)
        }
        camera.update()
    }

    private fun noise(seed: Float): Float {
        return Perlin.perlin(seed, time * frequency) * 2 - 1
    }

    fun shake(intensity: Float) {
        trauma = (trauma + intensity).coerceAtMost(1f)
    }

    fun setMin(intensity: Float) {
        trauma = trauma.coerceAtLeast(intensity)
    }

    private fun resetRotation() {
        // TODO det må finnes en bedre måte å gjøre dette på
        camera.up.set(Vector3.Y.cpy())
        camera.direction.set(-Vector3.Z)
    }
}
