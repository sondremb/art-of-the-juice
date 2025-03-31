package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.components.Component
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.vec2
import kotlin.reflect.KClass

open class Entity(val world: World, var position: Vector2) : Disposable {
    var velocity: Vector2 = vec2(0f, 0f)
    @PublishedApi internal val components: Map<KClass<*>, Component> = mutableMapOf()

    inline fun <reified T : Component> getComponent(): T {
        return components[T::class] as T
    }

    inline fun <reified T : Component> tryGetComponent(): T? {
        return components[T::class] as T?
    }

    inline operator fun <reified T : Component> T.unaryPlus() {
        addComponent(this)
    }

    inline fun <reified T : Component> addComponent(component: T) {
        (components as MutableMap)[T::class] = component
        component.entity = this
        component.init()
    }

    fun init() {
        components.values.forEach { it.lateInit() }
    }

    override fun dispose() {
        components.values.forEach { it.disposeSafely() }
    }

    open fun update(delta: Float) {
        components.values.forEach { it.update(delta) }
    }

    open fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        components.values.forEach { it.render(batch, shape) }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                it.circle(position.x, position.y, 1f)
            }
        }
    }
}
