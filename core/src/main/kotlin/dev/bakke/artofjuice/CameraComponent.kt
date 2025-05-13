package dev.bakke.artofjuice

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.components.Component
import ktx.math.plus

class CameraComponent(private var camera: Camera, private var player: Entity) : Component() {
    override fun update(delta: Float) {
        // bruker entity.position for å huske kameraets "egentlige" posisjon
        // nyttig i tilfelle camera.position blir forskjøvet av noe annet, f.eks. screenshake

        // OPPGAVE 5

        // kamera sentrert midt spillerens posisjon, pluss litt oppover
        entity.position.set(player.position + Vector2(0f, 72f))
        setCameraPosition()
    }

    private fun setCameraPosition() {
        camera.position.x = entity.position.x
        camera.position.y = entity.position.y
    }
}
