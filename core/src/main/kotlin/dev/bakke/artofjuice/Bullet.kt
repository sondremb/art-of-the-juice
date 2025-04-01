package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.SpriteComponent

fun World.createBullet(position: Vector2, direction: Vector2): Entity {
    val atlas = TextureAtlas("Bullets.atlas")
    val region = atlas.findRegion("2")
    val sprite = Sprite(region)
    return entity(position) {
        velocity = direction.cpy().nor().scl(300f)
        +BulletComponent()
        +SpriteComponent(sprite)
        +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 12f, 4f)), true)
    }
}

class BulletComponent : Component() {
    override fun lateInit() {
        val colliderComponent = getComponent<ColliderComponent>()
        colliderComponent.onCollision {
            entity.destroy()
        }
        colliderComponent.onTerrainCollision {
            entity.destroy()
        }
    }

    override fun update(delta: Float) {
        entity.position.x += entity.velocity.x * delta
        entity.position.y += entity.velocity.y * delta
    }
}
