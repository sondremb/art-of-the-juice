package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Camera
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.components.Component
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2

class CameraComponent(private var camera: Camera, private var player: Entity) : Component() {
    override fun update(delta: Float) {
        val target = player.position + vec2(0f, 72f)
        
        entity.position += (target - entity.position) * 0.1f
        camera.position.x = entity.position.x
        camera.position.y = entity.position.y
    }
}
