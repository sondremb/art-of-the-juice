package dev.bakke.artofjuice.player

import dev.bakke.artofjuice.engine.components.Component

class GrenadeComponent(
    private var fuseTime: Float,
    private var explosionRadius: Float,
    private var damage: Int) : Component() {
    private var timeSinceThrown = 0f

    override fun update(delta: Float) {
        timeSinceThrown += delta
        if (timeSinceThrown >= fuseTime) {
            spawnEntity(entity.position.cpy()) {
                +ExplosionComponent(explosionRadius, damage)
            }
            entity.destroy()
        }
    }
}
