package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas

data class GunStats(
    val damage: Int,
    val bulletSpeed: Float,
    val fireRate: Float,
    val gunSprite: Sprite,
    val bulletSprite: Sprite,
    val arms: PlayerArms,
    val impulse: Float,
    val shakeIntensity: Float = 0.1f,
) {
    companion object {
        val DEFAULT = GunStats(
            10,
            800f,
            0.05f,
            TextureAtlas("Weapons.atlas").findRegion("pistol1").let(::Sprite),
            TextureAtlas("Weapons.atlas").findRegion("pistol_bullet1").let(::Sprite),
            PlayerArms.One,
            100f,
            0.4f
        )
        val SNIPER = GunStats(
            80,
            1200f,
            0.4f,
            TextureAtlas("Weapons.atlas").findRegion("rifle6").let(::Sprite),
            TextureAtlas("Weapons.atlas").findRegion("rifle_bullet6").let(::Sprite),
            PlayerArms.Two,
            800f,
            0.6f
        )
    }
}
