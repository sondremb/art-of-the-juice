package dev.bakke.artofjuice.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import dev.bakke.artofjuice.Entity
import ktx.inject.Context

abstract class Component : Disposable {
    lateinit var entity: Entity
    val context: Context get() = entity.world.context
    var isActive: Boolean = true

    inline fun <reified T : Component> getComponent(): T {
        return entity.getComponent<T>()
    }

    inline fun <reified T : Component> tryGetComponent(): T? {
        return entity.tryGetComponent<T>()
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
