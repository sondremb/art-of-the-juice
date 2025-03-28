package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

abstract class Entity(var position: Vector2, var velocity: Vector2 = vec2(0f, 0f)) {
    lateinit var world: World

    abstract fun update(delta: Float)
    open fun render(batch: SpriteBatch, shape: ShapeRenderer) {}

    open val collider: Rectangle? = null
}
