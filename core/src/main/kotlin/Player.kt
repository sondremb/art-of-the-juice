import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y

class Player: Disposable {
    private var animations = PlayerAnimations()

    val position = Vector2(100f, 100f) // Start position
    val velocity = Vector2(0f, 0f) // Movement speed

    private val speed = 200f // Horizontal speed
    private val jumpForce = 600f
    private val gravity = -900f
    private var isOnGround = false
    private var isFacingRight = true
    private val bbox = Rectangle(position.x, position.y, 24f, 32f)

    private val jumpBufferTime = 0.1f
    private var jumpBuffer = 0f

    fun init(map: TiledMap) {
        val layer = map.layers.get("Player")
        layer.objects.get("Spawn").let {
            position.set(it.x, it.y)
        }
    }

    fun update(delta: Float, rects: Collection<Rectangle>) {
        // Apply gravity
        velocity.y += gravity * delta

        // Movement input
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x = -speed
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x = speed
        else velocity.x = 0f

        // Jumping
        val spaceJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        if (isOnGround && (spaceJustPressed || jumpBuffer > 0f)) {
            velocity.y = jumpForce
            isOnGround = false
            jumpBuffer = 0f
        } else if (!isOnGround && spaceJustPressed) {
            jumpBuffer = jumpBufferTime
        } else if (jumpBuffer > 0f) {
            jumpBuffer -= delta
        }


        // Update position
        val newPosition = position + (velocity * delta)
        if (!collidesWithMap(rects, vec2(newPosition.x, position.y))) {
           position.x = newPosition.x
        }
        if (collidesWithMap(rects, vec2(position.x, newPosition.y))) {
            isOnGround = velocity.y <= 0f
            velocity.y = 0f
        } else {
            position.y = newPosition.y
            isOnGround = false
        }


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
    }

    fun collidesWithMap(map: Collection<Rectangle>, newPosition: Vector2): Boolean {
        bbox.setPosition(newPosition)
        return map.any { bbox.overlaps(it) }.also { bbox.setPosition(position) }
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
        shape.use(ShapeRenderer.ShapeType.Line) {
            it.rect(bbox.x, bbox.y, bbox.width, bbox.height)
        }
    }

    override fun dispose() {
        animations.disposeSafely()
    }
}
