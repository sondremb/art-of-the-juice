package dev.bakke.artofjuice.gun

import ktx.async.schedule

data class GunStats(
    val damage: Int,
    // i units (=pixler) per sekund
    val bulletSpeed: Float,
    // i kuler per sekund
    val fireRate: Float,
    // OPPGAVE 2B
    val knockbackForce: Float,
    // OPPGAVE 3B
    val screenshakeAmount: Float,
    // nye stats kan legges til her
) {
    companion object {
        val PISTOL = GunStats(
            damage = 15,
            bulletSpeed = 800f,
            fireRate = 0.25f,
            knockbackForce = 100f,
            screenshakeAmount = 0.3f,
        )
        val RIFLE = GunStats(
            damage = 25,
            bulletSpeed = 1000f,
            fireRate = 0.15f,
            knockbackForce = 150f,
            screenshakeAmount = 0.45f,
        )
        val SNIPER = GunStats(
            damage = 80,
            bulletSpeed = 1200f,
            fireRate = 0.4f,
            knockbackForce = 800f,
            screenshakeAmount = 0.6f
        )
    }
}
