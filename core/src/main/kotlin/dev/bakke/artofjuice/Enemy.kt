package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.vec2

class Enemy(
    position: Vector2,
    private var physicsComponent: PhysicsComponent
    ) : Entity(position, vec2(0f, 0f)), Disposable {
    private var animations = SkaterAnimations()

    private val speed = 100f // Horizontal speed

    init {
        velocity.x = speed
    }

    override val collider = Rectangle(position.x, position.y, 24f, 32f)

    fun update(delta: Float, rects: Collection<Rectangle>) {
        // Apply gravity
        physicsComponent.update(this, delta, rects)
        animations.update(delta)
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        animations.getCurrentFrame()
        batch.use {
            batch.draw(animations.getCurrentFrame(), position.x, position.y)
        }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                shape.rect(collider)
            }
        }
    }

    override fun dispose() {
        animations.disposeSafely()
    }
}
