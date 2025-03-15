package dev.bakke.artofjuice

import Player
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

class PlayerInputComponent {
    private val speed = 200f
    private val jumpBufferTime = 0.1f
    private var jumpBuffer = 0f
    private val jumpForce = 600f

    fun update(player: Player, delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.velocity.x = -speed
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) player.velocity.x = speed
        else player.velocity.x = 0f

        // Jumping
        val spaceJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        if (player.isOnGround && (spaceJustPressed || jumpBuffer > 0f)) {
            player.velocity.y = jumpForce
            jumpBuffer = 0f
        } else if (!player.isOnGround && spaceJustPressed) {
            jumpBuffer = jumpBufferTime
        } else if (jumpBuffer > 0f) {
            jumpBuffer -= delta
        }
    }
}
