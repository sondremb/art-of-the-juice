package dev.bakke.artofjuice.gun

data class GunStats(
    val damage: Int,
    val bulletSpeed: Float,
    val fireRate: Float,
    val impulse: Float,
    val shakeIntensity: Float = 0.1f,
) {
    companion object {
        val DEFAULT = GunStats(
            10,
            800f,
            0.05f,
            100f,
            0.4f
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
