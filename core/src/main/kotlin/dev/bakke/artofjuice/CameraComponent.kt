package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.gdx.extensions.rect
import ktx.graphics.use
import ktx.math.plus
import ktx.math.vec2

class CameraComponent(private var camera: Camera, private var player: Entity) : Component() {
    private val deadzone = Rectangle(-20f, -10f, 40f, 20f)
    private val minHeight = 16f
    private val moveSpeedX = 6f
    private val moveSpeedY = 12f

    override fun update(delta: Float) {
        val target = player.position + vec2(0f, 72f)
        deadzone.setCenter(entity.position)
        if (target.x > deadzone.x && target.x < deadzone.x + deadzone.width) {
            target.x = entity.position.x
        }
        if (target.y > deadzone.y && target.y < deadzone.y + deadzone.height) {
            target.y = entity.position.y
        }
        val halfHeight = camera.viewportHeight / 2f
        // vil ikke ha nedre camera grense lavere enn minHeight ->
        // vil ikke ha target - halfHeight < minHeight ->
        // vil ikke ha target < minHeight + halfHeight
        target.y = target.y.coerceAtLeast(minHeight + halfHeight)

        entity.position.x += (target.x - entity.position.x) * moveSpeedX* delta
        camera.position.x = entity.position.x
        entity.position.y += (target.y - entity.position.y) * moveSpeedY * delta
        camera.position.y = entity.position.y
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val cameraMinY = minHeight + camera.viewportHeight / 2f
        val rawTarget = player.position + vec2(0f, 72f)
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                it.rect(rawTarget.x, rawTarget.y, 1f, 1f)
                it.line(-100f, cameraMinY, 1000f, cameraMinY)
                deadzone.setCenter(entity.position)
                it.rect(deadzone)
            }
        }
    }
}
