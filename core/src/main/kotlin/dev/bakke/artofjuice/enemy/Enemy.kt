package dev.bakke.artofjuice.enemy

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.Entity
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.components.AnimatedSpriteComponent
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.graphics.use
import ktx.math.vec2

class Enemy(
    position: Vector2,
    private var physicsComponent: PhysicsComponent,
    private var animatedSpriteComponent: AnimatedSpriteComponent<SkaterAnimatedSprite.State>
    ) : Entity(position, vec2(0f, 0f)), Disposable {

    private val speed = 100f // Horizontal speed

    init {
        velocity.x = speed
    }

    override val collider = Rectangle(position.x, position.y, 24f, 32f)

    fun update(delta: Float, rects: Collection<Rectangle>) {
        // Apply gravity
        physicsComponent.update(this, delta, rects)
        animatedSpriteComponent.update(this, delta)
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        batch.use {
            animatedSpriteComponent.render(this, it)
        }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                shape.rect(collider)
            }
        }
    }

    override fun dispose() {
    }
}
