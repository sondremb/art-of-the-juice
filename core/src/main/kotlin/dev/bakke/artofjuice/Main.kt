package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.collision.ColliderComponent
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.collision.CollisionSystem
import dev.bakke.artofjuice.player.*
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.inject.Context
import ktx.math.vec2
import ktx.tiled.x
import ktx.tiled.y

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
    private val context = Context()
    private val world = World(context)
    private val collisionSystem = CollisionSystem().apply { context.bindSingleton(this) }
    private val player = world.entity(vec2(100f, 100f)) {
        +Tag.PLAYER
        +PhysicsComponent(-900f)
        +PlayerInputComponent()
        +PlayerAnimatedSprite()
        +ColliderComponent(RectangleCollisionShape(Rectangle(0f, 0f, 24f, 32f)))
        +GunComponent(GunStats.DEFAULT)
    }
    private val enemySpawner = world.entity(vec2(0f, 0f)) {
        +SpawnEnemyComponent(0.5f)
    }
    private val camera = OrthographicCamera()
    private val screenshakeSystem = ScreenshakeSystem(camera, player).apply { context.bindSingleton(this) }
    private val debugUI = DebugUI(batch, player)
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer


    override fun show() {
        map = TmxMapLoader().load("map.tmx")
        player.position = map.layers.get("Player").objects.get("Spawn").let { vec2(it.x, it.y) }
        renderer = OrthogonalTiledMapRenderer(map)
        camera.setToOrtho(false, 800f, 600f) // Adjust to match your game window size
        map.layers.get("Player").objects.get("Enemy").let { enemySpawner.position.set(it.x, it.y) }

        val layer = map.layers.get("metal_collision")
        //layer.objects.map { (it as RectangleMapObject).rectangle }.let { world.rects.addAll(it) }
        layer.objects
            .map { (it as RectangleMapObject).rectangle }
            .forEach { collisionSystem.addTerrainCollider(RectangleCollisionShape(it)) }
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        world.update(delta)
        collisionSystem.update(delta)
        camera.position.set(player.position.x, player.position.y, 0f)
        screenshakeSystem.update(delta)
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        renderer.setView(camera)
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
    }
}
