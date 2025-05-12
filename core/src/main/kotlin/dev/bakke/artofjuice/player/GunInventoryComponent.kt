package dev.bakke.artofjuice.player

import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.gun.BulletSprites
import dev.bakke.artofjuice.gun.Gun
import dev.bakke.artofjuice.gun.GunComponent
import dev.bakke.artofjuice.gun.GunSprites
import dev.bakke.artofjuice.gun.GunStats
import dev.bakke.artofjuice.gun.GunVisualsManager
import ktx.assets.disposeSafely

class GunInventoryComponent() : Component() {
    private lateinit var gunVisualsManager: GunVisualsManager
    private lateinit var gunComponent: GunComponent
    private lateinit var guns: List<Gun>
    private var currentGun = 0


    override fun lateInit() {
        gunVisualsManager = getSystem()
        gunComponent = getComponent()
        guns = listOf(
            Gun(
                GunStats.Companion.SNIPER,
                gunVisualsManager.getVisualsBySpriteName(GunSprites.Rifle6),
                gunVisualsManager.getSpriteByName(BulletSprites.RifleBullet6)
            ),
            Gun(
                GunStats.Companion.PISTOL,
                gunVisualsManager.getVisualsBySpriteName(GunSprites.Pistol2),
                gunVisualsManager.getSpriteByName(BulletSprites.PistolBullet2)
            ),
            Gun(
                GunStats.Companion.RIFLE,
                gunVisualsManager.getVisualsBySpriteName(GunSprites.Rifle10),
                gunVisualsManager.getSpriteByName(BulletSprites.RifleBullet10)
            ),
        )
        gunComponent.gun = guns.first()
    }

    fun nextGun() {
        currentGun = (currentGun + 1) % guns.size
        gunComponent.gun = guns[currentGun]
    }

    override fun dispose() {
        gunVisualsManager.disposeSafely()
    }
}
