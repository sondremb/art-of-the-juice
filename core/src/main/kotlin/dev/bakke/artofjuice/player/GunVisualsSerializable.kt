package dev.bakke.artofjuice.player

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile

enum class PlayerArms {
    One,
    Two
}

object GunSprites {
    val Rifle1 = "rifle1"
    val Rifle2 = "rifle2"
    val Rifle3 = "rifle3"
    val Rifle4 = "rifle4"
    val Rifle5 = "rifle5"
    val Rifle6 = "rifle6"
    val Rifle7 = "rifle7"
    val Rifle8 = "rifle8"
    val Rifle9 = "rifle9"
    val Rifle10 = "rifle10"

    val Pistol1 = "pistol1"
    val Pistol2 = "pistol2"
    val Pistol3 = "pistol3"
    val Pistol4 = "pistol4"
    val Pistol5 = "pistol5"
    val Pistol6 = "pistol6"
    val Pistol7 = "pistol7"
    val Pistol8 = "pistol8"
    val Pistol9 = "pistol9"
    val Pistol10 = "pistol10"
}

object BulletSprites {
    val RifleBullet1 = "rifle_bullet1"
    val RifleBullet2 = "rifle_bullet2"
    val RifleBullet3 = "rifle_bullet3"
    val RifleBullet4 = "rifle_bullet4"
    val RifleBullet5 = "rifle_bullet5"
    val RifleBullet6 = "rifle_bullet6"
    val RifleBullet7 = "rifle_bullet7"
    val RifleBullet8 = "rifle_bullet8"
    val RifleBullet9 = "rifle_bullet9"
    val RifleBullet10 = "rifle_bullet10"

    val PistolBullet1 = "pistol_bullet1"
    val PistolBullet2 = "pistol_bullet2"
    val PistolBullet3 = "pistol_bullet3"
    val PistolBullet4 = "pistol_bullet4"
    val PistolBullet5 = "pistol_bullet5"
    val PistolBullet6 = "pistol_bullet6"
    val PistolBullet7 = "pistol_bullet7"
    val PistolBullet8 = "pistol_bullet8"
    val PistolBullet9 = "pistol_bullet9"
    val PistolBullet10 = "pistol_bullet10"
}

class GunVisuals(
    val sprite: Sprite,
    val arms: PlayerArms,
    val gunOffset: Vector2,
    val bulletOffset: Vector2
)

class GunVisualsManager : Disposable {
    private val atlas = TextureAtlas("weapons.atlas".toInternalFile())
    private lateinit var serializables: Array<GunVisualsSerializable>

    private fun fromSerializable(gunVisualsSerializable: GunVisualsSerializable): GunVisuals {
        return GunVisuals(
            getSpriteByName(gunVisualsSerializable.spriteName),
            gunVisualsSerializable.arms,
            gunVisualsSerializable.gunOffset,
            gunVisualsSerializable.bulletOffset
        )
    }

    fun loadJson() {
        val json = Json().apply { setOutputType(JsonWriter.OutputType.json) }
        serializables = json.fromJson(Array<GunVisualsSerializable>::class.java, "gunVisuals.json".toInternalFile())
    }

    fun getVisualsBySpriteName(spriteName: String): GunVisuals {
        val serializable = serializables.find { it.spriteName == spriteName }!!
        return fromSerializable(serializable)
    }

    fun getSpriteByName(spriteName: String): Sprite {
        return Sprite(atlas.findRegion(spriteName))
    }

    override fun dispose() {
        atlas.disposeSafely()
    }
}

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
