package dev.bakke.artofjuice.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import dev.bakke.artofjuice.*
import dev.bakke.artofjuice.enemy.SpawnEnemyComponent
import dev.bakke.artofjuice.engine.*
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.rendering.RenderPipeline
import dev.bakke.artofjuice.engine.rendering.ShaderPass
import dev.bakke.artofjuice.gun.GunVisualsManager
import dev.bakke.artofjuice.player.spawnPlayer
import dev.bakke.artofjuice.rendering.BloomPass
import dev.bakke.artofjuice.rendering.ShockwavePass
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.inject.Context
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y

class GameScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shape = ShapeRenderer()
    private val context = Context()
    private val world = World(context)
    private val collisionSystem = CollisionSystem().apply { context.bindSingleton(this) }
    private val particleSystem = ParticleSystem().apply { context.bindSingleton(this) }
    private val assets = Assets().apply {
        context.bindSingleton(this)
        loadAllBlocking()
    }
    private val gunVisualsManager = GunVisualsManager(assets).apply {
        context.bindSingleton(this)
    }
    private val player = world.spawnPlayer(vec2(100f, 100f))

    private val enemySpawner = world.spawnEntity(vec2(0f, 0f)) {
        +SpawnEnemyComponent(0.8f)
    }
    private val camera = OrthographicCamera()
    private val cameraEntity = world.spawnEntity(player.position.cpy()) {
        +CameraComponent(camera, player)
    }
    private val uiCamera = OrthographicCamera()
    private val screenshakeSystem = ScreenshakeSystem(camera, player).apply { context.bindSingleton(this) }
    private val debugUI = DebugUI(batch, player)
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private var shockwaveSystem = ShockwaveSystem().apply { context.bindSingleton(this) }
    private lateinit var pipeline: RenderPipeline

    override fun show() {
        assets.loadAll()
        gunVisualsManager.loadJson()
        pipeline =
            RenderPipeline(
                Gdx.graphics.width, Gdx.graphics.height, listOf(
                    ShockwavePass(shockwaveSystem, camera),
                    BloomPass(),
                    ShaderPass("shaders/scanline.frag".toInternalFile()),
                    ShaderPass("shaders/vignette.frag".toInternalFile()),
                    ShaderPass("shaders/barrel_distortion.frag".toInternalFile()),
                )
            )

        map = TmxMapLoader().load("map.tmx")
        player.position = map.layers.get("Player").objects.get("Spawn").let { vec2(it.x, it.y) }
        renderer = OrthogonalTiledMapRenderer(map)
        camera.setToOrtho(false, 400f, 300f) // Adjust to match your game window size
        uiCamera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        map.layers.get("Player").objects.get("Enemy").let { enemySpawner.position.set(it.x, it.y) }

        val layer = map.layers.get("metal_collision")
        layer.objects
            .map { (it as RectangleMapObject).rectangle }
            .forEach { collisionSystem.addTerrainCollider(RectangleCollisionShape(it)) }
    }

    override fun render(delta: Float) {
        val delta = delta.coerceAtMost(1 / 30f)
        world.update(delta)
        collisionSystem.update(delta)
        screenshakeSystem.update(delta)
        shockwaveSystem.update(delta)
        particleSystem.update(delta)
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        renderer.setView(camera)
        pipeline.render {
            clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
            renderer.render()
            world.render(batch, shape)
            collisionSystem.render(batch, shape)
            particleSystem.render(batch)

            if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
                GamePreferences.setRenderDebug(!GamePreferences.renderDebug())
            }
        }
        batch.projectionMatrix = uiCamera.combined
        shape.projectionMatrix = uiCamera.combined
        if (GamePreferences.renderDebug()) {
            debugUI.render(delta)
            screenshakeSystem.render(batch, shape)
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat() / 2
        camera.viewportHeight = height.toFloat() / 2
        camera.update()
        uiCamera.viewportHeight = height.toFloat()
        uiCamera.viewportWidth = width.toFloat()
        uiCamera.update()
        pipeline.resize(width, height)
    }

    override fun dispose() {
        map.disposeSafely()
        renderer.disposeSafely()
        batch.disposeSafely()
        shape.disposeSafely()
        debugUI.disposeSafely()
        gunVisualsManager.disposeSafely()
    }
}

