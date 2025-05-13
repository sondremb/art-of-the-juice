package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.gdx.extensions.rect
import ktx.graphics.use
import ktx.math.plus
import ktx.math.vec2

// Camera-komponent, basert på samme GDC talk som ScreenshakeSystem:
// "Math for Game Programmers: Juicing Your Cameras With Math" av Squirrel Eiserloh
// https://www.youtube.com/watch?v=tu-Qe66AvtY
class SondresCameraComponent(private var camera: Camera, private var player: Entity) : Component() {
    // spilleren må bevege seg mer enn deadzone.width og .height vekk fra midten før kameraet følger
    private val deadzone = Rectangle(-20f, -10f, 40f, 20f)
    // en minstehøyde som vi ikke vil at bunnen av kameraet skal være lavere enn
    private val minHeight = 16f
    // forskjellige hastigheter for x og y
    private val moveSpeedX = 6f
    private val moveSpeedY = 12f

    override fun update(delta: Float) {
        // bruker entity.position for å huske kameraets "egentlige" posisjon
        // nyttig i tilfelle camera.position blir forskjøvet av noe annet, f.eks. screenshake
        // kamera sentrert midt spillerens posisjon, pluss litt oppover
        val target = player.position + vec2(0f, 72f)
        deadzone.setCenter(entity.position)
        // sett target.x til nåværende posisjon.x hvis den er innenfor deadzone
        if (target.x > deadzone.x && target.x < deadzone.x + deadzone.width) {
            target.x = entity.position.x
        }
        // samme med y
        if (target.y > deadzone.y && target.y < deadzone.y + deadzone.height) {
            target.y = entity.position.y
        }

        val halfHeight = camera.viewportHeight / 2f
        // vil ikke ha nedre camera grense lavere enn minHeight ->
        // vil ikke ha target - halfHeight < minHeight ->
        // vil ikke ha target < minHeight + halfHeight
        target.y = target.y.coerceAtLeast(minHeight + halfHeight)

        // "asymptotic averaging" fra GDC-talken - vi beveger oss en brøkdel av avstanden til target per frame
        entity.position.x += (target.x - entity.position.x) * moveSpeedX * delta
        entity.position.y += (target.y - entity.position.y) * moveSpeedY * delta

        setCameraPosition()
    }

    private fun setCameraPosition() {
        camera.position.x = entity.position.x
        camera.position.y = entity.position.y
    }

    override fun render(batch: SpriteBatch, shape: ShapeRenderer) {
        val cameraMinY = minHeight + camera.viewportHeight / 2f
        val rawTarget = player.position + vec2(0f, 72f)
        if (GamePreferences.renderDebug()) {
            shape.use(ShapeRenderer.ShapeType.Line) {
                it.rect(rawTarget.x, rawTarget.y, 1f, 1f)
                it.line(-100f, cameraMinY, 1000f, cameraMinY)
                deadzone.setCenter(entity.position)
                it.rect(deadzone)
            }
        }
    }
}
