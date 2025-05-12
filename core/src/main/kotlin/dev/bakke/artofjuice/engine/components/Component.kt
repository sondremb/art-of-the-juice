package dev.bakke.artofjuice.engine.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.engine.Entity
import ktx.inject.Context
import kotlin.reflect.KClass

abstract class Component : Disposable {
    lateinit var entity: Entity
    val context: Context get() = entity.world.context
    var isActive: Boolean = true

    inline fun <reified T : Component> getComponentLazy(): Lazy<T> {
        return lazy { entity.getComponent<T>() }
    }

    inline fun <reified T : Component> getComponent(): T {
        return entity.getComponent<T>()
    }

    inline fun <reified T : Component> tryGetComponent(): T? {
        return entity.tryGetComponent<T>()
    }

    inline fun <reified  T : Any> getSystemLazy(): Lazy<T> {
        return lazy { entity.getSystem() }
    }

    inline fun <reified  T : Any> getSystem(): T {
        return entity.getSystem<T>()
    }

    fun removeFromEntity() {
        val kClass = this::class as KClass<Component>
        entity.removeComponent(kClass)
    }

    fun spawnEntity(position: Vector2, block: Entity.() -> Unit): Entity {
        return entity.spawnEntity(position, block)
    }

    /**
     * Called when the component is added to an entity
     */
    open fun init() {}

    /**
     * Called when entity is added to the world, after all components have been initialized
     */
    open fun lateInit() {}

    /**
     * Called when the component is removed from an entity
     */
    override fun dispose() {}

    open fun update(delta: Float) {}
    open fun render(batch: SpriteBatch, shape: ShapeRenderer) {}
}
