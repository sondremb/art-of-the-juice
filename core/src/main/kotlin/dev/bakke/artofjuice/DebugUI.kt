package dev.bakke.artofjuice

import Player
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import ktx.graphics.use

class DebugUI(private val batch: SpriteBatch, private val player: Player) : Disposable {
    private val font = BitmapFont()

    fun render() {
        batch.projectionMatrix.setToOrtho2D(0f, 0f, 800f, 600f)
        batch.use {
            font.draw(batch, "Pos: ${printVector2(player.position)}", 20f, 580f)
            font.draw(batch, "Vel: ${printVector2(player.velocity)}", 20f, 560f)
        }
    }

    private fun printVector2(vector: Vector2): String {
        return "(${"%.2f".format(vector.x)}, ${"%.2f".format(vector.y)})"
    }

    override fun dispose() {
        font.disposeSafely()
    }
}
