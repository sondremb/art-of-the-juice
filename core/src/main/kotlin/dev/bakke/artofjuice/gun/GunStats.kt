package dev.bakke.artofjuice.gun

data class GunStats(
    val damage: Int,
    val bulletSpeed: Float,
    val fireRate: Float,
    val impulse: Float,
    val shakeIntensity: Float = 0.1f,
) {
    companion object {
        val PISTOL = GunStats(
            15,
            800f,
            0.25f,
            100f,
            0.3f
        )
        val RIFLE = GunStats(
            25,
            1000f,
            0.15f,
            100f,
            0.45f
        )
        val SNIPER = GunStats(
            80,
            1200f,
            0.4f,
            800f,
            0.6f
        )
    }
}
