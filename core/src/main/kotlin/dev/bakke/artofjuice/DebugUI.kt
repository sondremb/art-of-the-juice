package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.engine.Entity
import ktx.assets.disposeSafely
import ktx.graphics.use

class DebugUI() : Disposable {
    lateinit var player: Entity
    private val font = BitmapFont()
    private var frames: List<Float> = listOf()

    fun update(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            GamePreferences.setRenderDebug(!GamePreferences.renderDebug())
        }
        frames = frames.map { it + delta }.dropWhile { it > 1 }.plus(delta)
    }

    fun render(batch: SpriteBatch) {
        val top = Gdx.graphics.height.toFloat()
        batch.use {
            font.draw(batch, "Pos: ${printVector2(player.position)}", 20f, top - 20f)
            font.draw(batch, "Vel: ${printVector2(player.velocity)}", 20f, top - 40f)
            font.draw(batch, "fps: ${frames.size}", 20f, top - 60f)
            font.draw(batch, "Entities: ${player.world.entities.items.size}", 20f, top - 80f)
        }
    }

    private fun printVector2(vector: Vector2): String {
        return "(%.2f, %.2f)".format(vector.x, vector.y)
    }

    override fun dispose() {
        font.disposeSafely()
    }
}
