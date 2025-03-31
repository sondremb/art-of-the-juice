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
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.enemy.SkaterAnimatedSprite
import dev.bakke.artofjuice.player.PlayerAnimatedSprite
import dev.bakke.artofjuice.player.PlayerInputComponent
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.graphics.use
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
    private val world = World()
    private val player = world.entity(vec2(100f, 100f)) {
        +PhysicsComponent(-900f)
        +PlayerInputComponent()
        +PlayerAnimatedSprite()
        collider = Rectangle(position.x, position.y, 24f, 32f)
    }
    private val enemy = world.entity(vec2(200f, 100f)) {
        +EnemyAIComponent()
        +PhysicsComponent(-900f)
        +SkaterAnimatedSprite()
        collider = Rectangle(position.x, position.y, 24f, 32f)
    }
    private val debugUI = DebugUI(batch, player)
    private lateinit var map: TiledMap
    private lateinit var renderer: OrthogonalTiledMapRenderer
    private lateinit var camera: OrthographicCamera


    override fun show() {
        map = TmxMapLoader().load("map.tmx")
        player.position = map.layers.get("Player").objects.get("Spawn").let { vec2(it.x, it.y) }
        renderer = OrthogonalTiledMapRenderer(map)
        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 600f) // Adjust to match your game window size
        map.layers.get("Player").objects.get("Enemy").let { enemy.position.set(it.x, it.y) }

        val layer = map.layers.get("metal_collision")
        layer.objects.map { (it as RectangleMapObject).rectangle }.let { world.rects.addAll(it) }
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        world.update(delta)
        camera.position.set(player.position.x, player.position.y, 0f)
        camera.update()
        batch.projectionMatrix = camera.combined
        shape.projectionMatrix = camera.combined
        renderer.setView(camera)
        renderer.render()
        world.render(batch, shape)

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            GamePreferences.setRenderDebug(!GamePreferences.renderDebug())
        }

        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                world.rects.forEach { rect ->
                    it.rect(rect.x, rect.y, rect.width, rect.height)
                }
            }
            debugUI.render(delta)
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
