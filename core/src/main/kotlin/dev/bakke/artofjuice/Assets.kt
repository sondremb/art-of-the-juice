package dev.bakke.artofjuice

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import ktx.assets.load

class Assets {
    private val assetManager = AssetManager()

    fun loadAll() {
        assetManager.load<TextureAtlas>(Atlases.Player)
        assetManager.load<TextureAtlas>(Atlases.Skater)
        assetManager.load<TextureAtlas>(Atlases.Bullets)
        assetManager.load<TextureAtlas>(Atlases.Weapons)
        assetManager.load<TextureAtlas>(Atlases.Effects)
    }

    fun loadAllBlocking() {
        loadAll()
        assetManager.finishLoading()
    }

    fun finishLoading() = assetManager.finishLoading()

    fun getRegion(asset: TextureRegionAsset): TextureRegion {
        return getRegion(asset.atlas, asset.region)
    }

    fun getRegion(atlas: String, region: String): TextureRegion {
        val atlas = assetManager.get<TextureAtlas>(atlas)
        return atlas.findRegion(region)
            ?: error("Missing region '$region' in atlas '$atlas'")
    }

    fun getRegions(asset: TextureRegionAsset): Array<TextureAtlas.AtlasRegion> {
        return getRegions(asset.atlas, asset.region)
    }

    fun getRegions(atlas: String, region: String): Array<TextureAtlas.AtlasRegion> {
        val atlas = assetManager.get<TextureAtlas>(atlas)
        return atlas.findRegions(region)
            ?: error("Missing region '$region' in atlas '$atlas'")
    }

    fun dispose() {
        assetManager.dispose()
    }
}

object Atlases {
    const val Weapons = "weapons.atlas"
    const val Effects = "Effects.atlas"
    const val Bullets = "Bullets.atlas"
    const val Skater = "Skater.atlas"
    const val Player = "new_character.atlas"
}

class TextureRegionAsset(val atlas: String, val region: String)

object TextureAssets {
    object Enemy {
        val Walk = TextureRegionAsset(Atlases.Skater, "Walk")
        val Idle = TextureRegionAsset(Atlases.Skater, "Idle")
        val Hurt = TextureRegionAsset(Atlases.Skater, "Hurt")
        val Death = TextureRegionAsset(Atlases.Skater, "Death")
    }

    object Weapon {
        val Rifle1 = TextureRegionAsset(Atlases.Weapons, "rifle1")
        val Rifle2 = TextureRegionAsset(Atlases.Weapons, "rifle2")
        val Rifle3 = TextureRegionAsset(Atlases.Weapons, "rifle3")
        val Rifle4 = TextureRegionAsset(Atlases.Weapons, "rifle4")
        val Rifle5 = TextureRegionAsset(Atlases.Weapons, "rifle5")
        val Rifle6 = TextureRegionAsset(Atlases.Weapons, "rifle6")
        val Rifle7 = TextureRegionAsset(Atlases.Weapons, "rifle7")
        val Rifle8 = TextureRegionAsset(Atlases.Weapons, "rifle8")
        val Rifle9 = TextureRegionAsset(Atlases.Weapons, "rifle9")
        val Rifle10 = TextureRegionAsset(Atlases.Weapons, "rifle10")
    }

    object Effects {
        val Effect6 = TextureRegionAsset(Atlases.Effects, "effect6")
        val Effect7 = TextureRegionAsset(Atlases.Effects, "effect7")
        val Effect8 = TextureRegionAsset(Atlases.Effects, "effect8")
        val Effect9 = TextureRegionAsset(Atlases.Effects, "effect9")
        val Effect10 = TextureRegionAsset(Atlases.Effects, "effect10")
        fun random() = listOf(
            Effect6,
            Effect7,
            Effect8,
            Effect9,
            Effect10
        ).random()
    }
}
