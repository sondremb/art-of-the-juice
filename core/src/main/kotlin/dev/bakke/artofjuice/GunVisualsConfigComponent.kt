package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.gun.GunComponent
import dev.bakke.artofjuice.gun.GunVisualsSerializable
import ktx.assets.toInternalFile

class GunVisualsConfigComponent(private val player1: Entity, private val player2: Entity) : Component() {
    private lateinit var gunComponent1: GunComponent
    private lateinit var gunComponent2: GunComponent
    private var json = Json().apply { this.setOutputType(JsonWriter.OutputType.json) }
    private val visuals = json.fromJson(Array<GunVisualsSerializable>::class.java, "gunVisuals.json".toInternalFile())
    private val originalVisuals = visuals.map { it.copy() }
    private var currentVisualsIndex = 0


    override fun lateInit() {
        gunComponent1 = player1.getComponent()
        gunComponent2 = player2.getComponent()
        //gunComponent1.gun?.visuals = visuals.first()
        //gunComponent2.gun?.visuals = visuals.first()
    }

    override fun update(delta: Float) {
        val stats1 = gunComponent1.gun!!
        val stats2 = gunComponent2.gun!!
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentVisualsIndex = (currentVisualsIndex + 1) % visuals.size
            //stats1.visuals = visuals[currentVisualsIndex]
            //stats2.visuals = visuals[currentVisualsIndex]
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            visuals[currentVisualsIndex] = originalVisuals[currentVisualsIndex].copy()
            //stats1.visuals = visuals[currentVisualsIndex]
            //stats2.visuals = visuals[currentVisualsIndex]
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
    }
}
