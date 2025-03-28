package dev.bakke.artofjuice.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.Entity

abstract class Component {
    lateinit var entity: Entity
    open fun update(delta: Float) {}
    open fun render(batch: SpriteBatch, shape: ShapeRenderer) {}
}
