package dev.bakke.artofjuice

import com.badlogic.gdx.math.Vector2

class ShockwaveSystem {
    var center: Vector2? = null
        private set
    var time = 0f
        private set
    var maxTime = 0f
        private set


    fun update(delta: Float) {
        if (center == null) {
            return
        }
        time += delta
        if (time >= maxTime) {
            center = null
        }
    }

    fun setExplosion(position: Vector2, duration: Float = 0.5f) {
        if (center != null) {
            return
        }
        center = position
        maxTime = duration
        time = 0f
    }
}
