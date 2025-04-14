package dev.bakke.artofjuice.engine.components

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.graphics.use

class SpriteComponent(val sprite: Sprite) : Component() {
    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        // Render sprite
        batch.use {
            sprite.setCenter(entity.position.x, entity.position.y)
            sprite.draw(it)
        }
    }
}
