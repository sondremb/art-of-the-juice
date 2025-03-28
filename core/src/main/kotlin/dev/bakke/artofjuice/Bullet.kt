package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.components.SpriteComponent

class Bullet(
    position: Vector2,
    private val spriteComponent: SpriteComponent) : Entity(position) {

    override fun update(delta: Float) {
        position.x += velocity.x * delta
        position.y += velocity.y * delta
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        // Render bullet
        spriteComponent.render(this, batch)
    }
}

fun createBullet(position: Vector2, velocity: Vector2): Bullet {
    val atlas = TextureAtlas("Bullets.atlas")
    val region = atlas.findRegion("2")
    val sprite = Sprite(region)
    val spriteComponent = SpriteComponent(sprite)
    return Bullet(position, spriteComponent).apply {
        this.velocity = velocity
    }
}
