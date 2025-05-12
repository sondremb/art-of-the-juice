package dev.bakke.artofjuice.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.*
import dev.bakke.artofjuice.engine.collision.CollisionSystem
import dev.bakke.artofjuice.engine.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.World
import dev.bakke.artofjuice.gun.*
import dev.bakke.artofjuice.player.*
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.inject.Context
import ktx.math.vec2

class ConfigGunVisualsScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shape = ShapeRenderer()
    private val context = Context()
    private val world = World(context)
    private val collisionSystem = CollisionSystem().apply { context.bindSingleton(this) }.apply {
        addTerrainCollider(RectangleCollisionShape(Rectangle(-100f, 0f, 100f, 600f)))
        addTerrainCollider(RectangleCollisionShape(Rectangle(800f, 0f, 100f, 600f)))
        addTerrainCollider(RectangleCollisionShape(Rectangle(0f, -100f, 800f, 100f)))
        addTerrainCollider(RectangleCollisionShape(Rectangle(0f, 600f, 800f, 100f)))
    }
    val assets = Assets().apply { context.bindSingleton(this) }
    val gunVisualsManager = GunVisualsManager(assets).apply {
        loadJson()
        context.bindSingleton(this)
    }
    val gun = Gun(
        GunStats.SNIPER.copy(bulletSpeed = 100f),
        gunVisualsManager.getVisualsBySpriteName(GunSprites.Rifle1),
        gunVisualsManager.getSpriteByName(BulletSprites.RifleBullet6))
    private val player1 = world.spawnEntity(vec2(300f, 300f)) {
        +Tag.PLAYER
        +AutoShooterComponent(-1f)
        +PlayerVisuals()
        +GunComponent(gun)
    }
    private val player2 = world.spawnEntity(vec2(500f, 300f)) {
        +Tag.PLAYER
        +AutoShooterComponent(1f)
        +PlayerVisuals()
        +GunComponent(gun)
    }
    private val controller = world.spawnEntity(vec2(0f, 0f)) {
        +GunVisualsConfigComponent(player1, player2)
    }

    // registrert, men updates ikke
    private val screenshakeSystem =
        ScreenshakeSystem(OrthographicCamera()).apply { context.bindSingleton(this) }
    private val camera = OrthographicCamera()
    private val debugUI = DebugUI(batch, player1)


    override fun show() {
        camera.setToOrtho(false, 800f, 600f) // Adjust to match your game window size
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        world.update(delta)
        collisionSystem.update(delta)
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        world.render(batch, shape)
        collisionSystem.render(batch, shape)

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            GamePreferences.setRenderDebug(!GamePreferences.renderDebug())
        }

        if (GamePreferences.renderDebug()) {
            debugUI.render(delta)
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    override fun dispose() {
        batch.disposeSafely()
        shape.disposeSafely()
        debugUI.disposeSafely()
    }
}

class AutoShooterComponent(private var direction: Float) : Component() {
    private lateinit var gunComponent: GunComponent
    override fun lateInit() {
        entity.getComponent<PlayerVisuals>().flipX = direction < 0
        gunComponent = getComponent()
    }

    override fun update(delta: Float) {
        gunComponent.shoot(vec2(direction, 0f))
    }
}

