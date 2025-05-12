package dev.bakke.artofjuice.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import dev.bakke.artofjuice.gun.*
import ktx.math.vec2

class PlayerInputComponent : Component() {

    private val physicsComponent: PhysicsComponent by getComponentLazy()
    private val animatedSpriteComponent: PlayerVisuals by getComponentLazy()
    private val gunComponent: GunComponent by getComponentLazy()
    private val grenadeComponent: GrenadeThrowerComponent by getComponentLazy()
    private val gunInventoryComponent: GunInventoryComponent by getComponentLazy()

    override fun update(delta: Float) {
        handleMove()
        handleJump(delta)
        handleShoot()
        handleThrowGrenade()
        handleSwitchGun()
    }

    // how fast the player moves, in units per second
    private val speed = 10 * 32f
    private var isFacingRight = true
    private fun handleMove() {
        // hmm, maybe some acceleration and deceleration would be nice here
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            entity.velocity.x = -speed
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            entity.velocity.x = speed
        } else {
            entity.velocity.x = 0f
        }

        if (physicsComponent.isOnGround) {
            if (entity.velocity.x == 0f) {
                animatedSpriteComponent.requestTransition(PlayerVisuals.State.IDLE)
            } else {
                animatedSpriteComponent.requestTransition(PlayerVisuals.State.RUN)
            }
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && entity.velocity.x != 0f) {
            if (entity.velocity.x > 0f) {
                isFacingRight = true
            } else if (entity.velocity.x < 0f) {
                isFacingRight = false
            }
        }

        animatedSpriteComponent.flipX = !isFacingRight
    }

    // parameterized jump, thanks to the GDC talk "Math for Game Programmers: Building a Better Jump" by Kyle Pittman
    // https://www.youtube.com/watch?v=hG9SzQxaCm8&t=1325s
    // how high the player can jump, in units, which presently equal pixels
    // one tile in the game is 32x32 pixels
    private val jumpHeight = 6.5f * 32f

    // how much distance the player will travel in the x direction before reaching the peak of the jump, in units
    private val jumpLength = 4 * 32f

    // derived values, don't change these
    private val jumpVelocity = 2 * jumpHeight * speed / jumpLength
    private val upwardsGravity = -2 * jumpHeight * speed * speed / (jumpLength * jumpLength)

    // how much gravity to apply after the player releases the jump button, before their speed turns negative
    private val releaseGravity = upwardsGravity * 3f
    // how much gravity to apply when the player is falling, i.e. when their speed is negative
    private val fallGravity = upwardsGravity * 1.2f
    // how long before the player hits the ground a jump can be "buffered", so that it activates when the player lands
    private val jumpBufferTime = 0.1f
    // how long after the player leaves the ground they can still jump, i.e. when they are in the air
    // named after Wile E. Coyote from Looney Tunes, who can run off a cliff and still be in the air for a short time
    private val coyoteTime = 0.1f

    // timer values, let these be 0f
    private var jumpBuffer = 0f
    private var coyoteTimer = 0f

    private fun handleJump(delta: Float) {
        if (physicsComponent.isOnGround) {
            coyoteTimer = coyoteTime
        } else if (coyoteTimer > 0f) {
            coyoteTimer -= delta
        }

        val spaceJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        val canJump = physicsComponent.isOnGround || coyoteTimer > 0f
        val wantsJump = spaceJustPressed || jumpBuffer > 0f
        if (canJump && wantsJump) {
            jump()
        } else if (!physicsComponent.isOnGround && spaceJustPressed) {
            jumpBuffer = jumpBufferTime
        } else if (jumpBuffer > 0f) {
            jumpBuffer -= delta
        }

        physicsComponent.gravity = when {
            entity.velocity.y < 0f -> fallGravity
            Gdx.input.isKeyPressed(Input.Keys.SPACE) -> upwardsGravity
            else -> releaseGravity
        }
        entity.velocity.y = entity.velocity.y.coerceAtLeast(-900f)
    }

    private fun jump() {
        entity.velocity.y = jumpVelocity
        animatedSpriteComponent.requestTransition(PlayerVisuals.State.JUMP)
        jumpBuffer = 0f
        coyoteTimer = 0f
    }

    private fun handleShoot() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            val direction = if (isFacingRight) 1f else -1f
            gunComponent.shoot(
                vec2(direction, 0f)
            )
        }
    }

    private fun handleThrowGrenade() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            grenadeComponent.throwGrenade(vec2(if (isFacingRight) 1f else -1f, 1f))
        }
    }

    private fun handleSwitchGun() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            gunInventoryComponent.nextGun()
        }
    }
}
