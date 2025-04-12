package dev.bakke.artofjuice.player

data class GunStats(
    var visuals: GunVisuals = GunVisuals("pistol1", PlayerArms.One),
    val bulletSprite: String,
    val damage: Int,
    val bulletSpeed: Float,
    val fireRate: Float,
    val impulse: Float,
    val shakeIntensity: Float = 0.1f,
) {
    companion object {
        val DEFAULT = GunStats(
            GunVisuals.DEFAULT,
            "pistol_bullet1",
            10,
            800f,
            0.05f,
            100f,
            0.4f
        )
        val SNIPER = GunStats(
            GunVisuals.SNIPER,
            "rifle_bullet6",
            80,
            1200f,
            0.4f,
            800f,
            0.6f
        )
    }
}
