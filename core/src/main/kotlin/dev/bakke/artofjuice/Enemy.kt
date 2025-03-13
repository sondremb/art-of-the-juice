package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.plus
import ktx.math.times
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.gdx.extensions.rect
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y

class Enemy : Disposable{
    private var animations = SkaterAnimations()

    val position = Vector2(100f, 100f) // Start position
    val velocity = Vector2(0f, 0f) // Movement speed

    private val speed = 100f // Horizontal speed
    private val gravity = -900f

    private val bbox = Rectangle(position.x, position.y, 24f, 32f)

    fun init(map: TiledMap) {
        val layer = map.layers.get("Player")
        layer.objects.get("Enemy").let {
            position.set(it.x, it.y)
        }
        animations.setState(SkaterAnimations.State.RUN)
        velocity.x = speed
    }

    fun update(delta: Float, rects: Collection<Rectangle>) {
        // Apply gravity
        velocity.y += gravity * delta

        // Update position
        val newPosition = position + (velocity * delta)
        if (!collidesWithMap(rects, vec2(position.x, newPosition.y))) {
            position.y = newPosition.y
        } else {
            velocity.y = 0f
        }
        if (collidesWithMap(rects, vec2(newPosition.x, position.y))) {
            velocity.x *= -1
        } else {
            position.x = newPosition.x
        }
        bbox.setPosition(position)

        animations.update(delta)
    }

    fun collidesWithMap(map: Collection<Rectangle>, newPosition: Vector2): Boolean {
        val box = Rectangle(newPosition.x, newPosition.y, bbox.width, bbox.height)
        return map.any { box.overlaps(it) }
    }

    fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        animations.getCurrentFrame()
        batch.use {
            batch.draw(animations.getCurrentFrame(), position.x, position.y)
        }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                shape.rect(bbox)
            }
        }
    }

    override fun dispose() {
        animations.disposeSafely()
    }


}
