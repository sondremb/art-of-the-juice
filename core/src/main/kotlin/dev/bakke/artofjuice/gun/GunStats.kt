package dev.bakke.artofjuice.gun

data class GunStats(
    val damage: Int,
    // i units (=pixler) per sekund
    val bulletSpeed: Float,
    // i kuler per sekund
    val fireRate: Float,
    val shakeIntensity: Float = 0.1f,
) {
    companion object {
        val PISTOL = GunStats(
            damage = 15,
            bulletSpeed = 800f,
            fireRate = 0.25f,
            0.3f
        )
        val RIFLE = GunStats(
            damage = 25,
            bulletSpeed = 1000f,
            fireRate = 0.15f,
            0.45f
        )
        val SNIPER = GunStats(
            damage = 80,
            bulletSpeed = 1200f,
            fireRate = 0.4f,
            0.6f
        )
    }
}
