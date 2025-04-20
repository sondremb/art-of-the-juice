package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.engine.Entity
import ktx.assets.disposeSafely
import ktx.graphics.use

class DebugUI(private val batch: SpriteBatch, private val player: Entity) : Disposable {
    private val font = BitmapFont()
    private var frames: List<Float> = listOf()

    fun render(delta: Float) {
        frames = frames.map { it + delta }.dropWhile { it > 1 }.plus(delta)
        val h = Gdx.graphics.height.toFloat()
        batch.use {
            font.draw(batch, "Pos: ${printVector2(player.position)}", 20f, h - 20f)
            font.draw(batch, "Vel: ${printVector2(player.velocity)}", 20f, h - 40f)
            font.draw(batch, "fps: ${frames.size}", 20f, h -60f)
            font.draw(batch, "Entities: ${player.world.entities.items.size}", 20f, h - 80f)
        }
    }

    private fun printVector2(vector: Vector2): String {
        return "(%.2f, %.2f)".format(vector.x, vector.y)
    }

    override fun dispose() {
        font.disposeSafely()
    }
}
