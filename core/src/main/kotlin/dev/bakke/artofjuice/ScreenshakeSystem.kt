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
// TL;DW:
// * man har "trauma", som går fra 0 til 1
// * trauma legges til av ting som vil forårsake screenshake, f.eks. eksplosjoner
// * trauma går lineært nedover med tid, mister en viss mengde per sekund
// * bruker trauma^2 eller ^3 som faktor for å gange med maks utslag på screenshake,
// * bruker Perlin-noise for kontinuerlig og smooth screenshake
// for mer om hvorfor Perlin-Noise er en sykt god idé til dette og alt annet, se denne GDC-talken av samme person:
// "Math for Game Programmers: Noise-Based RNG" av Squirrel Eiserloh
// https://www.youtube.com/watch?v=LWFzPP8ZbdU
class ScreenshakeSystem(private val camera: Camera) {
    // parametere du kan tukle med:
    // trauma tapt per sekund
    private var traumaDecay = 0.8f
    // maks utslag på screenshake, i units (=pixels)
    private var maxAmplitude = 50f
    // "frekvens" på Perlin-noise - hvor fort skjermen rister
    private var frequency = 10f
    // maks rotasjon på kamera, i grader
    private var maxRotationDegrees = 5f
    private var seed = 0.2387f; // guaranteed random!

    // la disse starte som 0f du
    private var trauma = 0f
    private var time = 0f

    fun update(delta: Float) {
        time += delta
        resetRotation()
        if (trauma > 0f) {
            trauma = (trauma - delta * traumaDecay).coerceAtLeast(0f)
            val t3 = trauma * trauma * trauma
            val amplitude = maxAmplitude * t3
            val offsetX = noise(seed) * amplitude
            val offsetY = noise(seed + 1) * amplitude
            val rotation = noise(seed + 2) * maxRotationDegrees * t3
            camera.position.x += offsetX
            camera.position.y += offsetY
            camera.rotate(Vector3.Z, rotation)
        }
        camera.update()
    }

    // additiv screenshake - legger til mer risting, kan stackes
    fun addScreenShake(intensity: Float) {
        trauma = (trauma + intensity).coerceAtMost(1f)
    }

    // minimum screenshake - setter minsteverdi, stacker ikke
    fun setMinimumShake(intensity: Float) {
        trauma = trauma.coerceAtLeast(intensity)
    }

    private fun noise(seed: Float): Float {
        return Perlin.perlin(seed, time * frequency) * 2 - 1
    }

    private fun resetRotation() {
        // TODO det må finnes en bedre måte å gjøre dette på
        camera.up.set(Vector3.Y.cpy())
        camera.direction.set(-Vector3.Z)
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!GamePreferences.renderDebug()) {
            return
        }
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
