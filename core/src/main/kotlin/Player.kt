import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.Entity
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.PhysicsComponent
import dev.bakke.artofjuice.PlayerInputComponent
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.assets.disposeSafely
import ktx.graphics.use

class Player(position: Vector2, private var physicsComponent: PhysicsComponent, private var input: PlayerInputComponent) : Entity(position), Disposable {
    private var animations = PlayerAnimations()

    val isOnGround: Boolean
        get () = physicsComponent.isOnGround
    private var isFacingRight = true
    override val collider = Rectangle(position.x, position.y, 24f, 32f)

    fun update(delta: Float, rects: Collection<Rectangle>) {
        input.update(this, delta)
        physicsComponent.update(this, delta, rects)

        if (velocity.x < 0f) {
            isFacingRight = false
        } else if (velocity.x > 0f) {
            isFacingRight = true
        }

        animations.update(delta)
        if (velocity.x != 0f) {
            animations.setState(PlayerAnimations.State.RUN)
        } else {
            animations.setState(PlayerAnimations.State.IDLE)
        }
    }

    fun render(batch: Batch, shape: ShapeRenderer) {
        val frame = animations.getCurrentFrame()
        val scaleX = if (isFacingRight) 1f else -1f
        batch.use {
            it.draw(
                frame,
                position.x + if (isFacingRight) 0f else frame.regionWidth.toFloat(),
                position.y,
                frame.regionWidth.toFloat() * scaleX,
                frame.regionHeight.toFloat()
            )
        }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                it.rect(collider)
            }
        }
    }

    override fun dispose() {
        animations.disposeSafely()
    }
}
