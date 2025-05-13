package dev.bakke.artofjuice

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ktx.tiled.x
import ktx.tiled.y

class MapLoader(assets: Assets) : Disposable {
    private val map = assets.getMap()
    val renderer = OrthogonalTiledMapRenderer(map)

    fun getPlayerPosition() = map
        .layers.get(Layers.PLAYER)
        .objects.get(Objects.PLAYER)
        .let { Vector2(it.x, it.y) }

    fun getEnemySpawnerPosition() = map
        .layers.get(Layers.PLAYER)
        .objects.get(Objects.ENEMY_SPAWN)
        .let { Vector2(it.x, it.y) }

    fun getCollisionRects() = map
        .layers.get(Layers.COLLISION)
        .objects
        .map {  (it as RectangleMapObject).rectangle  }

    override fun dispose() {

    }

}

object Layers {
    const val PLAYER = "Player"
    const val COLLISION = "metal_collision"
}

object Objects {
    const val PLAYER = "Spawn"
    const val ENEMY_SPAWN = "Enemy"
}
