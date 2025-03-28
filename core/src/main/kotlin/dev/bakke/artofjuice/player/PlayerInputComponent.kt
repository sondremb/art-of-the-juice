package dev.bakke.artofjuice.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import dev.bakke.artofjuice.components.Component
import dev.bakke.artofjuice.components.PhysicsComponent
import dev.bakke.artofjuice.createBullet
import ktx.math.vec2

class PlayerInputComponent : Component() {
    private val speed = 10 * 32f
    // parameterized jump, thanks to the GDC talk "Math for Game Programmers: Building a Better Jump" by Kyle Pittman
    // https://www.youtube.com/watch?v=hG9SzQxaCm8&t=1325s
    private val jumpHeight = 6.5f * 32f
    // how much distance the player will travel in the x direction before reaching the peak of the jump
    private val jumpLength = 4 * 32f

    private val jumpVelocity = 2 * jumpHeight * speed / jumpLength
    private val upwardsGravity = -2 * jumpHeight * speed * speed / (jumpLength * jumpLength)
    private val releaseGravity = upwardsGravity * 3f
    private val fallGravity = upwardsGravity * 1.2f

    private val jumpBufferTime = 0.1f
    private var jumpBuffer = 0f

    private val coyoteTime = 0.1f
    private var coyoteTimer = 0f
    private var isFacingRight = true

    override fun update(delta: Float) {
        val player = entity
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.velocity.x = -speed
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.velocity.x = speed
        else player.velocity.x = 0f
        val physicsComponent = player.getComponent<PhysicsComponent>()!!
        val animatedSpriteComponent = player.getComponent<PlayerAnimatedSprite>()!!
        if (player.velocity.x == 0f) {
            animatedSpriteComponent.setState(PlayerAnimatedSprite.State.IDLE)
        } else {
            animatedSpriteComponent.setState(PlayerAnimatedSprite.State.RUN)
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && player.velocity.x != 0f) {
            if (player.velocity.x > 0f) {
                isFacingRight = true
            } else if (player.velocity.x < 0f) {
                isFacingRight = false
            }
        }
        animatedSpriteComponent.flipX = !isFacingRight
        if (physicsComponent.isOnGround) {
            coyoteTimer = coyoteTime
        } else if (coyoteTimer > 0f) {
            coyoteTimer -= delta
        }
        // Jumping
        val spaceJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        if ((physicsComponent.isOnGround || coyoteTimer > 0f) && (spaceJustPressed || jumpBuffer > 0f)) {
            player.velocity.y = jumpVelocity
            jumpBuffer = 0f
            coyoteTimer = 0f
        } else if (!physicsComponent.isOnGround && spaceJustPressed) {
            jumpBuffer = jumpBufferTime
        } else if (jumpBuffer > 0f) {
            jumpBuffer -= delta
        }
        physicsComponent.gravity =  when {
            player.velocity.y < 0f -> fallGravity
            Gdx.input.isKeyPressed(Input.Keys.SPACE) -> upwardsGravity
            else -> releaseGravity
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            player.velocity.x *= 0.7f
            val direction = if (isFacingRight) 1f else -1f
            player.world.addEntity(
                createBullet(
                    vec2(player.position.x + direction * 16f, player.position.y + 16f),
                    vec2(direction * 500f, 0f)))
        }
    }
}
