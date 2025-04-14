package dev.bakke.artofjuice.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.DebugUI
import dev.bakke.artofjuice.GamePreferences
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.enemy.SpawnEnemyComponent
import dev.bakke.artofjuice.engine.World
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import dev.bakke.artofjuice.gun.GunComponent
import dev.bakke.artofjuice.gun.GunVisualsManager
import dev.bakke.artofjuice.player.GrenadeThrowerComponent
import dev.bakke.artofjuice.player.GunInventoryComponent
import dev.bakke.artofjuice.player.PlayerInputComponent
import dev.bakke.artofjuice.player.PlayerVisuals
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.graphics.use
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
    private val gunVisualsManager = GunVisualsManager().apply { loadJson() }
    private val player = world.spawnEntity(vec2(100f, 100f)) {
        +Tag.PLAYER
        +PhysicsComponent(-900f)
        +PlayerInputComponent()
        +PlayerVisuals()
        +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 24f, 32f)))
        +GunComponent(null)
        +GunInventoryComponent()
        +GrenadeThrowerComponent()
    }
    private val enemySpawner = world.spawnEntity(vec2(0f, 0f)) {
        +SpawnEnemyComponent(0.8f)
    }
    private val camera = OrthographicCamera()
    private val screenshakeSystem = ScreenshakeSystem(camera, player).apply { context.bindSingleton(this) }
    private val debugUI = DebugUI(batch, player)
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    lateinit var frameBuffer: FrameBuffer
    lateinit var shader: ShaderProgram
    lateinit var shaderBatch: SpriteBatch


    override fun show() {
        frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)
        shader = ShaderProgram("shaders/shockwave.vert".toInternalFile(), "shaders/shockwave.frag".toInternalFile())

        if (!shader.isCompiled) {
            Gdx.app.error("Shader", "Compilation failed:\n" + shader.log)
        }

        shaderBatch = SpriteBatch(1000, shader)
        map = TmxMapLoader().load("map.tmx")
        player.position = map.layers.get("Player").objects.get("Spawn").let { vec2(it.x, it.y) }
        renderer = OrthogonalTiledMapRenderer(map)
        camera.setToOrtho(false, 800f, 600f) // Adjust to match your game window size
        map.layers.get("Player").objects.get("Enemy").let { enemySpawner.position.set(it.x, it.y) }

        val layer = map.layers.get("metal_collision")
        layer.objects
            .map { (it as RectangleMapObject).rectangle }
            .forEach { collisionSystem.addTerrainCollider(RectangleCollisionShape(it)) }
    }

    override fun render(delta: Float) {
        world.update(delta)
        collisionSystem.update(delta)
        camera.position.set(player.position.x, player.position.y, 0f)
        screenshakeSystem.update(delta)
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        renderer.setView(camera)
        frameBuffer.use {
            clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
            renderer.render()
            world.render(batch, shape)
            collisionSystem.render(batch, shape)

            if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
                GamePreferences.setRenderDebug(!GamePreferences.renderDebug())
            }

            if (GamePreferences.renderDebug()) {
                debugUI.render(delta)
                screenshakeSystem.render(batch, shape)
            }
        }

        shaderBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shaderBatch.use {
            it.draw(frameBuffer.colorBufferTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), 0f, 0f, 1f, 1f)
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
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
