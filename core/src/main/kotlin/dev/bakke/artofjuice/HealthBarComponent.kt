package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.lerp
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.Event.Event
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.gdx.extensions.rect
import ktx.graphics.use
import ktx.math.plus

class HealthBarComponent(private var offset: Vector2, private var width: Float, height: Float) : Component() {
    private lateinit var healthComponent: HealthComponent
    private val rect = Rectangle(0f, 0f, width, height)
    private var shownHealth: Int = 0
    private var waitTime = 0.6f
    private var animationTime = 0.2f
    private var timeSinceLastDamage = waitTime
    var animationFinished = Event()

    override fun lateInit() {
        healthComponent = getComponent()
        shownHealth = healthComponent.health
        healthComponent.onDamage += ::onDamage
    }

    override fun update(delta: Float) {
        if (!isActive) return
        val timeRemaining = waitTime + animationTime - timeSinceLastDamage
        timeSinceLastDamage += delta
        if (timeRemaining > 0f && delta >= timeRemaining) {
            animationFinished.invoke()
        }
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!isActive) return
        rect.setCenter(entity.position + offset)
        val health = healthComponent.health
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
            rect.width = width
            shape.rect(rect)
        }
        shape.color = Color.WHITE
    }

    private fun onDamage(damageTaken: Int) {
        if (timeSinceLastDamage > waitTime) {
            shownHealth = healthComponent.health + damageTaken
        }
        timeSinceLastDamage = 0f
        if (healthComponent.health <= 0f) {
            timeSinceLastDamage = timeSinceLastDamage.coerceAtLeast(waitTime)
        }
    }

    private fun getWidth(health: Int): Float {
        return width * (health.toFloat() / healthComponent.maxHealth)
    }
}
