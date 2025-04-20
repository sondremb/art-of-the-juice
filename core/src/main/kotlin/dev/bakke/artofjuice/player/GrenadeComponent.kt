package dev.bakke.artofjuice.player

import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.ShockwaveSystem

class GrenadeComponent(
    private var fuseTime: Float,
    private var explosionRadius: Float,
    private var damage: Int) : Component() {
    private var timeSinceThrown = 0f

    override fun update(delta: Float) {
        timeSinceThrown += delta
        if (timeSinceThrown >= fuseTime) {
            getSystem<ShockwaveSystem>().addExplosion(entity.position.cpy(), 0.5f)
            spawnEntity(entity.position.cpy()) {
                +ExplosionComponent(explosionRadius, damage)
            }
            entity.destroy()
        }
    }
}
