package dev.bakke.artofjuice.gun

data class GunStats(
    val damage: Int,
    // i units (=pixler) per sekund
    val bulletSpeed: Float,
    // i kuler per sekund
    val fireRate: Float,
) {
    companion object {
        val PISTOL = GunStats(
            damage = 15,
            bulletSpeed = 800f,
            fireRate = 0.25f,
        )
        val RIFLE = GunStats(
            damage = 25,
            bulletSpeed = 1000f,
            fireRate = 0.15f,
        )
        val SNIPER = GunStats(
            damage = 80,
            bulletSpeed = 1200f,
            fireRate = 0.4f,
        )
    }
}
