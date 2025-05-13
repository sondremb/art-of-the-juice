package dev.bakke.artofjuice.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.bakke.artofjuice.*
import dev.bakke.artofjuice.enemy.SpawnEnemyComponent
import dev.bakke.artofjuice.engine.*
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.rendering.RenderPipeline
import dev.bakke.artofjuice.player.spawnPlayer
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.inject.Context
import ktx.math.vec2

class GameScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shape = ShapeRenderer()
    private val context = Context()
    private val systems = Systems(context)
    private val world = World(context)

    private val player = world.spawnPlayer(vec2(100f, 100f))

    private val enemySpawner = world.spawnEntity(vec2(0f, 0f)) {
        +SpawnEnemyComponent(0.8f)
    }
    private val camera = OrthographicCamera()
    private val uiCamera = OrthographicCamera()
    private val debugUI = DebugUI()
    private lateinit var pipeline: RenderPipeline
    private lateinit var mapLoader: MapLoader

    override fun show() {
        world.spawnEntity(player.position.cpy()) {
            +CameraComponent(camera, player)
        }
        systems.assets.loadAllBlocking()
        systems.screenshakeSystem.camera = camera
        systems.gunVisualsManager.loadJson()
        debugUI.player = player
        pipeline = RenderPipeline(
            Gdx.graphics.width, Gdx.graphics.height, listOf(
//                ShockwavePass(systems.shockwaveSystem, camera),
//                BloomPass(),
//                ShaderPass("shaders/scanline.frag".toInternalFile()),
//                ShaderPass("shaders/vignette.frag".toInternalFile()),
//                ShaderPass("shaders/barrel_distortion.frag".toInternalFile()),
            )
        )

        mapLoader = MapLoader(systems.assets)
        player.position = mapLoader.getPlayerPosition()
        enemySpawner.position = mapLoader.getEnemySpawnerPosition()
        camera.setToOrtho(false, 400f, 300f)
        uiCamera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        mapLoader.getCollisionRects()
            .forEach { systems.collisionSystem.addTerrainCollider(RectangleCollisionShape(it)) }
    }

    override fun render(delta: Float) {
        // lar ikke delta time bli større enn 1/30s, da kan skjer morsomme ting med fysikk
        val delta = delta.coerceAtMost(1 / 30f)

        world.update(delta)
        systems.update(delta)
        debugUI.update(delta)

        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        mapLoader.renderer.setView(camera)

        pipeline.render {
            clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
            mapLoader.renderer.render()
            world.render(batch, shape)
            systems.render(batch, shape)
        }
        batch.projectionMatrix = uiCamera.combined
        shape.projectionMatrix = uiCamera.combined
        if (GamePreferences.renderDebug()) {
            systems.screenshakeSystem.render(batch, shape)
            debugUI.render(batch)
        }
    }

    override fun resize(width: Int, height: Int) {
        // vil ha en 2:1 pixel ratio
        camera.viewportWidth = width.toFloat() / 2
        camera.viewportHeight = height.toFloat() / 2
        camera.update()
        uiCamera.viewportHeight = height.toFloat()
        uiCamera.viewportWidth = width.toFloat()
        uiCamera.update()
        pipeline.resize(width, height)
    }

    override fun dispose() {
        mapLoader.disposeSafely()
        batch.disposeSafely()
        shape.disposeSafely()
        debugUI.disposeSafely()
        systems.disposeSafely()
    }
}

