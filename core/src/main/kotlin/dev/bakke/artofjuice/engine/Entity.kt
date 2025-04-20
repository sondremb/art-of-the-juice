package dev.bakke.artofjuice.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.engine.components.Component
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.vec2
import kotlin.reflect.KClass

open class Entity(val world: World, var position: Vector2) : Disposable {
    private val tags = mutableSetOf<String>()
    private var isActive: Boolean = true
    var velocity: Vector2 = vec2(0f, 0f)
    @PublishedApi internal val components: MutableMap<KClass<*>, Component> = mutableMapOf()

    val hasTag: (String) -> Boolean = { tags.contains(it) }

    fun spawnEntity(position: Vector2, block: Entity.() -> Unit): Entity {
        return world.spawnEntity(position, block)
    }

    inline fun <reified  T : Any> getSystem(): T {
        return world.context.inject<T>()
    }

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
        components[T::class] = component
        component.entity = this
        component.init()
    }

    fun removeComponent(kClass: KClass<out Component>) {
        components[kClass]?.dispose()
        components.remove(kClass)
    }

    inline fun <reified  T : Component> removeComponent() {
        removeComponent(T::class)
    }

    operator fun String.unaryPlus() {
        tags.add(this)
    }

    fun init() {
        components.values.forEach { it.lateInit() }
    }

    fun destroy() {
        this.isActive = false
        components.values.forEach { it.isActive = false }
        world.destroyEntity(this)
    }

    override fun dispose() {
        components.values.forEach { it.disposeSafely() }
    }

    open fun update(delta: Float) {
        if (!isActive) return
        components.values.forEach { it.update(delta) }
    }

    open fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        if (!isActive) return
        components.values.forEach { it.render(batch, shape) }
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                it.circle(position.x, position.y, 1f)
            }
        }
    }
}
