package dev.bakke.artofjuice.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import dev.bakke.artofjuice.ScreenshakeSystem
import dev.bakke.artofjuice.engine.components.Component
import dev.bakke.artofjuice.engine.components.PhysicsComponent
import dev.bakke.artofjuice.gun.*
import ktx.assets.disposeSafely
import ktx.math.vec2

class PlayerInputComponent : Component() {

    private lateinit var physicsComponent: PhysicsComponent
    private lateinit var animatedSpriteComponent: PlayerVisuals
    private lateinit var gunComponent: GunComponent
    private lateinit var grenadeComponent: GrenadeThrowerComponent
    private lateinit var screenshakeSystem: ScreenshakeSystem
    private lateinit var gunInventoryComponent: GunInventoryComponent
    override fun lateInit() {
        physicsComponent = getComponent()
        animatedSpriteComponent = getComponent()
        gunComponent = getComponent()
        grenadeComponent = getComponent()
        screenshakeSystem = context.inject()
        gunInventoryComponent = getComponent()
    }

    override fun update(delta: Float) {
        handleMove()
        handleJump(delta)
        handleShoot()
        handleThrowGrenade()
        handleSwitchGun()
    }

    private val speed = 10 * 32f
    private var isFacingRight = true
    private fun handleMove() {
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
    private val jumpHeight = 6.5f * 32f

    // how much distance the player will travel in the x direction before reaching the peak of the jump
    private val jumpLength = 4 * 32f

    // derived values, don't change these
    private val jumpVelocity = 2 * jumpHeight * speed / jumpLength
    private val upwardsGravity = -2 * jumpHeight * speed * speed / (jumpLength * jumpLength)

    private val releaseGravity = upwardsGravity * 3f
    private val fallGravity = upwardsGravity * 1.2f
    private val jumpBufferTime = 0.1f
    private var jumpBuffer = 0f
    private val coyoteTime = 0.1f
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

class GunInventoryComponent() : Component() {
    private lateinit var gunVisualsManager: GunVisualsManager
    private lateinit var gunComponent: GunComponent
    private lateinit var guns: List<Gun>
    private var currentGun = 0
    override fun lateInit() {
        gunVisualsManager = GunVisualsManager().apply { loadJson() }
        gunComponent = getComponent()
        guns = listOf(
            Gun(
                GunStats.SNIPER,
                gunVisualsManager.getVisualsBySpriteName(GunSprites.Rifle6),
                gunVisualsManager.getSpriteByName(BulletSprites.RifleBullet6)),
            Gun(
                GunStats.PISTOL,
                gunVisualsManager.getVisualsBySpriteName(GunSprites.Pistol2),
                gunVisualsManager.getSpriteByName(BulletSprites.PistolBullet2)),
            Gun(
                GunStats.RIFLE,
                gunVisualsManager.getVisualsBySpriteName(GunSprites.Rifle10),
                gunVisualsManager.getSpriteByName(BulletSprites.RifleBullet10)
            ),
        )
        gunComponent.gun = guns.first()
    }

    fun nextGun() {
        currentGun = (currentGun + 1) % guns.size
        gunComponent.gun = guns[currentGun]
    }

    override fun dispose() {
        gunVisualsManager.disposeSafely()
    }
}
