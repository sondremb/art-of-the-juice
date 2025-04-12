package dev.bakke.artofjuice.player

import com.badlogic.gdx.math.Vector2

enum class PlayerArms {
    One,
    Two
}

class GunVisuals {
    constructor(spriteName: String, arms: PlayerArms) {
        this.spriteName = spriteName
        this.arms = arms
    }
    constructor(){}

    lateinit var spriteName: String
    lateinit var arms: PlayerArms
    var gunOffset: Vector2 = Vector2.Zero.cpy()
    var bulletOffset: Vector2 = Vector2.Zero.cpy()

    companion object {
        val DEFAULT = GunVisuals("pistol1", PlayerArms.One)
        val SNIPER = GunVisuals("rifle6", PlayerArms.Two)
    }

    fun copy(): GunVisuals {
        val copy = GunVisuals()
        copy.spriteName = spriteName
        copy.arms = arms
        copy.gunOffset = gunOffset.cpy()
        copy.bulletOffset = bulletOffset.cpy()
        return copy
    }
}
