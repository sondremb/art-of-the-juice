import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely

class Player: Disposable {
    private var animations = PlayerAnimations()

    private val position = Vector2(100f, 100f) // Start position
    private val velocity = Vector2(0f, 0f) // Movement speed

    private val speed = 200f // Horizontal speed
    private val jumpForce = 400f
    private val gravity = -900f
    private var isOnGround = false
    private var isFacingRight = true

    fun update(delta: Float) {
        // Apply gravity
        velocity.y += gravity * delta

        // Movement input
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x = -speed
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x = speed
        else velocity.x = 0f

        // Jumping
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround) {
            velocity.y = jumpForce
            isOnGround = false
        }

        // Update position
        position.add(velocity.x * delta, velocity.y * delta)

        if (velocity.x < 0) {
            isFacingRight = false
        } else if (velocity.x > 0) {
            isFacingRight = true
        }

        animations.update(delta)
        if (velocity.x != 0f) {
            animations.setState(PlayerAnimations.State.RUN)
        } else {
            animations.setState(PlayerAnimations.State.IDLE)
        }

        // Simulated ground collision
        if (position.y <= 100f) { // Assume ground is at y=100
            position.y = 100f
            velocity.y = 0f
            isOnGround = true
        }
    }

    fun render(batch: Batch) {
        val frame = animations.getCurrentFrame()
        val scaleX = if (isFacingRight) 1f else -1f
        batch.draw(
            frame,
            position.x + if (isFacingRight) 0f else frame.regionWidth.toFloat(),
            position.y,
            frame.regionWidth.toFloat() * scaleX,
            frame.regionHeight.toFloat()
        )
    }

    override fun dispose() {
        animations.disposeSafely()
    }
}
