package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.components.Component
import ktx.math.vec2
import kotlin.reflect.KClass

open class Entity(var position: Vector2, var velocity: Vector2 = vec2(0f, 0f)) {
    @PublishedApi internal val components: Map<KClass<*>, Component> = mutableMapOf()

    inline fun <reified T : Component> getComponent(): T? {
        return components[T::class] as T?
    }

    inline fun <reified T : Component> addComponent(component: T) {
        (components as MutableMap)[T::class] = component
        component.entity = this
    }
    lateinit var world: World

    open fun update(delta: Float) {
        components.values.forEach { it.update(delta) }
    }
    open fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        components.values.forEach { it.render(batch, shape) }
    }

    open var collider: Rectangle? = null
}
