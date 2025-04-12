package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import dev.bakke.artofjuice.collision.CollisionSystem
import dev.bakke.artofjuice.collision.shapes.RectangleCollisionShape
import dev.bakke.artofjuice.components.Component
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
    private val player1 = world.entity(vec2(300f, 300f)) {
        +Tag.PLAYER
        +AutoShooterComponent(-1f)
        +PlayerVisuals()
        +GunComponent(GunStats.SNIPER.copy(bulletSpeed = 100f))
    }
    private val player2 = world.entity(vec2(500f, 300f)) {
        +Tag.PLAYER
        +AutoShooterComponent(1f)
        +PlayerVisuals()
        +GunComponent(GunStats.SNIPER.copy(bulletSpeed = 100f))
    }
    private val controller = world.entity(vec2(0f, 0f)) {
        +MasterMindComponent(player1, player2)
    }

    // registrert, men updates ikke
    private val screenshakeSystem =
        ScreenshakeSystem(OrthographicCamera(), player1).apply { context.bindSingleton(this) }
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

class MasterMindComponent(private val player1: Entity, private val player2: Entity) : Component() {
    private lateinit var gunComponent1: GunComponent
    private lateinit var gunComponent2: GunComponent
    private var json = Json().apply { this.setOutputType(JsonWriter.OutputType.json) }
    private val visuals = json.fromJson(Array<GunVisuals>::class.java, Gdx.files.internal("gunVisuals.json"))
    private val originalVisuals = visuals.map { it.copy() }
    private var currentVisualsIndex = 0


    override fun lateInit() {
        gunComponent1 = player1.getComponent()
        gunComponent2 = player2.getComponent()
        gunComponent1.stats?.visuals = visuals.first()
        gunComponent2.stats?.visuals = visuals.first()
    }

    override fun update(delta: Float) {
        val stats1 = gunComponent1.stats!!
        val stats2 = gunComponent2.stats!!
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentVisualsIndex = (currentVisualsIndex + 1) % visuals.size
            stats1.visuals = visuals[currentVisualsIndex]
            gunComponent1.updateVisuals()
            stats2.visuals = visuals[currentVisualsIndex]
            gunComponent2.updateVisuals()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            visuals[currentVisualsIndex] = originalVisuals[currentVisualsIndex].copy()
            stats1.visuals = visuals[currentVisualsIndex]
            gunComponent1.updateVisuals()
            stats2.visuals = visuals[currentVisualsIndex]
            gunComponent2.updateVisuals()
        }
        val currentVisuals = visuals[currentVisualsIndex]
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            currentVisuals.gunOffset.x -= 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            currentVisuals.gunOffset.x += 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            currentVisuals.gunOffset.y += 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            currentVisuals.gunOffset.y -= 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            currentVisuals.bulletOffset.x -= 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            currentVisuals.bulletOffset.x += 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            currentVisuals.bulletOffset.y += 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            currentVisuals.bulletOffset.y -= 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            println(json.toJson(visuals))
        }
        gunComponent1.updateVisuals()
        gunComponent2.updateVisuals()
    }
}
