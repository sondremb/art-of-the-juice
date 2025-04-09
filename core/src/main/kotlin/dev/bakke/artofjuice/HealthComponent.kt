package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.lerp
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
    private var timeSinceLastDamage = 0f
    private var shownHealth = maxHealth
    private var waitTime = 1f
    private var animationTime = 0.2f

    override fun update(delta: Float) {
        timeSinceLastDamage += delta
        if (health <= 0) {
            entity.destroy()
        }
    }

    fun damage(amount: Int) {
        if (timeSinceLastDamage > waitTime) {
            shownHealth = health
        }
        health = (health - amount).coerceAtLeast(0)
        timeSinceLastDamage = 0f
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val rect = Rectangle(0f, 0f, healthBarWidth, healthBarHeight)
            .setCenter(entity.position + healthBarOffset)
        if (timeSinceLastDamage <= waitTime + animationTime) {
            val t = ((timeSinceLastDamage - waitTime) / animationTime).coerceIn(0f, 1f)
            val t2 = Interpolation.pow3In.apply(t)
            val healthToRender = lerp(shownHealth.toFloat(), health.toFloat(), t2)
            shape.use(ShapeRenderer.ShapeType.Filled) {
                shape.color = Color.WHITE
                rect.width = getWidth(healthToRender.toInt())
                shape.rect(rect)
            }
        } else if (timeSinceLastDamage > waitTime + animationTime) {
            shownHealth = health
        }
        shape.use(ShapeRenderer.ShapeType.Filled) {
            shape.color = Color.RED
            rect.width = getWidth(health)
            shape.rect(rect)
        }
        shape.use(ShapeRenderer.ShapeType.Line) {
            shape.color = Color.WHITE
            rect.width = healthBarWidth
            shape.rect(rect)
        }
        shape.color = Color.WHITE
    }

    private fun getWidth(health: Int): Float {
        return healthBarWidth * (health.toFloat() / maxHealth)
    }
}
