package dev.bakke.artofjuice.components

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dev.bakke.artofjuice.Entity
import ktx.graphics.use

class SpriteComponent(val sprite: Sprite) {
    fun update(delta: Float) {
        // Update sprite
    }

    fun render(entity: Entity, spriteBatch: SpriteBatch) {
        // Render sprite
        spriteBatch.use {
            it.draw(sprite, entity.position.x, entity.position.y)
        }
    }
}
