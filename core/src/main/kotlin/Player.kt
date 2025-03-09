import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y

class Player: Disposable {
    private var animations = PlayerAnimations()

    val position = Vector2(100f, 100f) // Start position
    private val velocity = Vector2(0f, 0f) // Movement speed

    private val speed = 200f // Horizontal speed
    private val jumpForce = 600f
    private val gravity = -900f
    private var isOnGround = false
    private var isFacingRight = true

    fun init(map: TiledMap) {
        val layer = map.layers.get("Player")
        layer.objects.get("Spawn").let {
            position.set(it.x, it.y)
        }
    }

    fun update(delta: Float, map: TiledMap) {
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
        val newPosition = position + (velocity * delta)
        if (!collidesWithMap(map, newPosition)) {
            position.set(newPosition)
        } else if (!collidesWithMap(map, vec2(newPosition.x, position.y))) {
            velocity.y = 0f
            isOnGround = true
            position.x = newPosition.x
        } else if (!collidesWithMap(map, vec2(position.x, newPosition.y))) {
            position.y = newPosition.y
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

    fun collidesWithMap(map: TiledMap, position: Vector2): Boolean {
        val layer = map.layers.get("Colission") as TiledMapTileLayer
        val cell = layer.getCell((position.x / layer.tileWidth).toInt(), (position.y / layer.tileHeight).toInt())
        return cell != null
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
