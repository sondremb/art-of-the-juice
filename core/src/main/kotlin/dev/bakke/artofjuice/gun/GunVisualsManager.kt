package dev.bakke.artofjuice.gun

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile

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
