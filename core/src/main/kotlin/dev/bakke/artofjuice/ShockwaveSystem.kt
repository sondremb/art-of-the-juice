package dev.bakke.artofjuice

import com.badlogic.gdx.math.Vector2

data class Shockwave(val position: Vector2, val duration: Float) {
    var time = 0f
}

class ShockwaveSystem {
    var shockwaves: MutableList<Shockwave> = mutableListOf()
        private set

    fun update(delta: Float) {
        shockwaves = shockwaves.filter {
            it.time += delta
            it.time <= it.duration
        }.toMutableList()
    }

    fun addExplosion(position: Vector2, duration: Float = 0.5f) {
        shockwaves.add(Shockwave(position, duration))
    }
}
