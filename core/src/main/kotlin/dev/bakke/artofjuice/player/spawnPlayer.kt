package dev.bakke.artofjuice.player

import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.World
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import dev.bakke.artofjuice.gun.GunComponent

fun World.spawnPlayer(position: Vector2): Entity {
    return this.spawnEntity(position) {
        +Tag.PLAYER
        +PhysicsComponent(-900f)
        +PlayerInputComponent()
        +PlayerVisuals()
        +ColliderComponent(RectangleCollisionShape(24f, 32f))
        +GunComponent(null)
        +GunInventoryComponent()
        +GrenadeThrowerComponent()
    }
}
