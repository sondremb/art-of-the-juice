package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.components.AnimatedSpriteComponent
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.graphics.use

class Player(
    position: Vector2,
    var physicsComponent: PhysicsComponent,
    private var input: PlayerInputComponent,
    var animatedSpriteComponent: AnimatedSpriteComponent<PlayerAnimatedSprite.State>
) : Entity(position), Disposable {

    val isOnGround: Boolean
        get () = physicsComponent.isOnGround
    override val collider = Rectangle(position.x, position.y, 24f, 32f)

    fun update(delta: Float, rects: Collection<Rectangle>) {
        input.update(this, delta)
        physicsComponent.update(this, delta, rects)
        animatedSpriteComponent.update(this, delta)
    }

    fun render(batch: Batch, shape: ShapeRenderer) {
        batch.use {
            animatedSpriteComponent.render(this, it)
        }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                it.rect(collider)
            }
        }
    }

    override fun dispose() {
    }
}
