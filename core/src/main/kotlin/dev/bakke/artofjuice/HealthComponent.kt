package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.graphics.use
import ktx.math.plus
import ktx.math.vec2

class HealthComponent(private val maxHealth: Int) : Component() {
    private var health = maxHealth
    private val healthBarOffset = vec2(0f, 20f)
    private val healthBarWidth = 32f
    private val healthBarHeight = 4f

    override fun update(delta: Float) {
        if (health <= 0) {
            entity.destroy()
        }
    }

    fun damage(amount: Int) {
        health -= amount
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val rect = Rectangle(0f, 0f, healthBarWidth, healthBarHeight)
            .setCenter(entity.position + healthBarOffset)
        shape.use(ShapeRenderer.ShapeType.Filled) {
            shape.color = Color.RED
            rect.width = healthBarWidth * (health.toFloat() / maxHealth)
            shape.rect(rect)
        }
        shape.use(ShapeRenderer.ShapeType.Line) {
            shape.color = Color.WHITE
            rect.width = healthBarWidth
            shape.rect(rect)
        }
        shape.color = Color.WHITE
    }
}
