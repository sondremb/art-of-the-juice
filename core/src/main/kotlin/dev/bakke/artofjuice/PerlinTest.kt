package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

class PerlinTest : KtxScreen {
    private val shape = ShapeRenderer()
    private val camera = OrthographicCamera()
    private var frequency = 0.1f
    private var timeFrequency = 0.1f
    private var time = 0f
    private var xScroll = 0f
    private var yScroll = 0f
    private val perlinOctave = PerlinOctave(
        frequency = frequency,
        amplitude = 1f,
        persistence = 0.5f,
        octaves = 1
    )

    override fun show() {
        camera.setToOrtho(false, 800f, 600f)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            timeFrequency += 0.1f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            timeFrequency -= 0.1f
        }
        timeFrequency = timeFrequency.coerceIn(0.1f, 10f)
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            frequency -= 0.01f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            frequency += 0.01f
        }
        frequency = frequency.coerceIn(0.01f, 1f)
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            yScroll += 1f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            yScroll -= 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            xScroll -= 1f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            xScroll += 1f
        }

        time += delta * timeFrequency
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        camera.update()
        shape.projectionMatrix = camera.combined
        shape.use(ShapeRenderer.ShapeType.Filled) {
            for (x in 0 until 800) {
                for (y in 0 until 600) {
                    val noise = perlinOctave.noise(
                        x * frequency + time * xScroll,
                        y * frequency + time * yScroll,
                        time)
                    shape.color = Color(noise, noise, noise, 1f)
                    shape.rect(x.toFloat(), y.toFloat(), 1f, 1f)
                }
            }
        }
    }

    override fun dispose() {
        shape.disposeSafely()
    }
}
