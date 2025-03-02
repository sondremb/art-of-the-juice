import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile

class Player {
    private val texture = Texture("logo.png".toInternalFile()) // Load player sprite
    private val position = Vector2(100f, 100f) // Start position
    private val velocity = Vector2(0f, 0f) // Movement speed
    private val bounds = Rectangle(position.x, position.y, texture.width.toFloat(), texture.height.toFloat())

    private val speed = 200f // Horizontal speed
    private val jumpForce = 400f
    private val gravity = -900f
    private var isOnGround = false

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

        // Simulated ground collision
        if (position.y <= 100f) { // Assume ground is at y=100
            position.y = 100f
            velocity.y = 0f
            isOnGround = true
        }

        // Update bounding box
        bounds.setPosition(position.x, position.y)
    }

    fun render(batch: Batch) {
        batch.draw(texture, position.x, position.y)
    }

    fun disposeSafely() {
        texture.disposeSafely()
    }
}
