package dev.bakke.artofjuice

import Player
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.graphics.use

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shape = ShapeRenderer()
    private val player = Player()
    private val debugUI = DebugUI(batch, player)
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private lateinit var camera: OrthographicCamera
    val rects = mutableListOf<Rectangle>()

    override fun show() {
        map = TmxMapLoader().load("map.tmx")
        renderer = OrthogonalTiledMapRenderer(map)
        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 600f) // Adjust to match your game window size
        player.init(map)

        val layer = map.layers.get("metal_collision")
        layer.objects.map { (it as RectangleMapObject).rectangle }.let { rects.addAll(it) }
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        player.update(delta, rects)
        camera.position.set(player.position.x, player.position.y, 0f)
        camera.update()
        batch.projectionMatrix = camera.combined
        renderer.setView(camera)
        renderer.render()
        player.render(batch, shape)
        shape.projectionMatrix = camera.combined
        shape.use(ShapeRenderer.ShapeType.Line) {
            rects.forEach { rect ->
                it.rect(rect.x, rect.y, rect.width, rect.height)
            }
        }

        debugUI.render()
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    override fun dispose() {
        map.disposeSafely()
        renderer.disposeSafely()
        player.disposeSafely()
        batch.disposeSafely()
        shape.disposeSafely()
        debugUI.disposeSafely()
    }
}
