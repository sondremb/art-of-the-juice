package dev.bakke.artofjuice.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import dev.bakke.artofjuice.*
import dev.bakke.artofjuice.enemy.SpawnEnemyComponent
import dev.bakke.artofjuice.engine.*
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.gun.GunVisualsManager
import dev.bakke.artofjuice.player.spawnPlayer
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.graphics.use
import ktx.inject.Context
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y
import kotlin.math.exp

class GameScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shape = ShapeRenderer()
    private val context = Context()
    private val world = World(context)
    private val collisionSystem = CollisionSystem().apply { context.bindSingleton(this) }
    private val gunVisualsManager = GunVisualsManager().apply { loadJson() }
    private val player = world.spawnPlayer(vec2(100f, 100f))

    private val enemySpawner = world.spawnEntity(vec2(0f, 0f)) {
        +SpawnEnemyComponent(0.8f)
    }
    private val camera = OrthographicCamera()
    private val screenshakeSystem = ScreenshakeSystem(camera, player).apply { context.bindSingleton(this) }
    private val debugUI = DebugUI(batch, player)
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private var shockwaveSystem = ShockwaveSystem().apply { context.bindSingleton(this) }
    private lateinit var pipeline: RenderPipeline

    override fun show() {
        pipeline =
            RenderPipeline(
                Gdx.graphics.width, Gdx.graphics.height, listOf(
                    ShockwavePass(shockwaveSystem, camera),
                    //GaussianPass(true),
                    //GaussianPass(false),
                    BloomPass(),
                    //ShaderPass("shaders/default.vert".toInternalFile(), "shaders/scanline.frag".toInternalFile())
                )
            )

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
        screenshakeSystem.update(delta)
        shockwaveSystem.update(delta)
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        renderer.setView(camera)
        val texture = pipeline.getTexture {
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
        val texture2 = pipeline.render(texture)
        pipeline.renderToScreen(texture2)
    }

    fun worldToUV(world: Vector2): Vector2 {
        // 1. Project world coords to screen space
        val projected = camera.project(Vector3(world.x, world.y, 0f))

        // 2. Normalize to UV [0, 1]
        val uvX = projected.x / Gdx.graphics.width
        val uvY = projected.y / Gdx.graphics.height

        return vec2(uvX, uvY)
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
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

class GaussianPass(isY: Boolean) :
    ShaderPass("shaders/default.vert".toInternalFile(), "shaders/bloom_blur.frag".toInternalFile()) {
    private var direction = if (isY) Vector2.Y.cpy() else Vector2.X.cpy()

    override fun beforeRender() {
        shader.use {
            it.setUniformf("u_direction", direction)
        }
    }
}

class BloomPass() : Renderpass {
    val thresholdShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_threshold.frag".toInternalFile())
    val gaussianShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_blur.frag".toInternalFile())
    val addShader: ShaderProgram =
        ShaderProgram("shaders/default.vert".toInternalFile(), "shaders/bloom_add.frag".toInternalFile())
    val thresholdBatch = SpriteBatch(1000, thresholdShader)
    val gaussianBatch = SpriteBatch(1000, gaussianShader)
    val addBatch = SpriteBatch(1000, addShader)

    override fun render(inputTexture: Texture, buffers: PingPongBuffer) {
        val inputCopyBuffer = FrameBuffer(Pixmap.Format.RGBA8888, inputTexture.width, inputTexture.height, false)
        val inputCopyBatch = SpriteBatch()
        inputCopyBuffer.use {  buffer ->
            inputCopyBatch.use {
                it.draw(inputTexture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f)
            }
        }
        val inputCopy = inputCopyBuffer.colorBufferTexture
        inputCopyBatch.dispose()

        var texture = inputTexture
        var buffer = buffers.write
        thresholdShader.use {
            it.setUniformf("u_threshold", 0.8f)
        }
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            thresholdBatch.use {
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()

        texture = buffers.read.colorBufferTexture
        gaussianShader.use {
            it.setUniformf("u_screenSize", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            it.setUniformf("u_direction", Vector2.Y.cpy())
        }
        buffer = buffers.write
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            gaussianBatch.use {
                clearScreen(1f, 1f, 1f, 0f)
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()

        texture = buffers.read.colorBufferTexture
        gaussianShader.use {
            it.setUniformf("u_screenSize", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            it.setUniformf("u_direction", Vector2.X.cpy())
        }
        buffer = buffers.write
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            gaussianBatch.use {
                it.draw(
                    texture,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        buffers.swap()

        texture = buffers.read.colorBufferTexture
        addShader.use {
            it.setUniformf("u_intensity", 5f)
            texture.bind(1)
            it.setUniformi("u_bloom", 1)
            inputCopy.bind(0)
            it.setUniformi("u_texture", 0)
        }
        buffer = buffers.write
        buffer.use {
            clearScreen(0f, 0f, 0f, 0f)
            addBatch.use {
                it.draw(
                    inputCopy,
                    0f,
                    0f,
                    buffer.width.toFloat(),
                    buffer.height.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            }
        }
        texture = buffer.colorBufferTexture
        buffers.swap()
    }
}
