package dev.bakke.artofjuice.gun

import com.badlogic.gdx.math.Vector2

class GunVisualsSerializable {
    constructor(spriteName: String, arms: PlayerArms) {
        this.spriteName = spriteName
        this.arms = arms
    }

    constructor() {}

    lateinit var spriteName: String
    lateinit var arms: PlayerArms
    var gunOffset: Vector2 = Vector2.Zero.cpy()
    var bulletOffset: Vector2 = Vector2.Zero.cpy()

    companion object {
        val DEFAULT = GunVisualsSerializable("pistol1", PlayerArms.One)
        val SNIPER = GunVisualsSerializable("rifle6", PlayerArms.Two)
    }

    fun copy(): GunVisualsSerializable {
        val copy = GunVisualsSerializable()
        copy.spriteName = spriteName
        copy.arms = arms
        copy.gunOffset = gunOffset.cpy()
        copy.bulletOffset = bulletOffset.cpy()
        return copy
    }
}
