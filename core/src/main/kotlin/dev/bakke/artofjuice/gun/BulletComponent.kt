package dev.bakke.artofjuice.gun

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Vector2
import dev.bakke.artofjuice.Assets
import dev.bakke.artofjuice.HealthComponent
import dev.bakke.artofjuice.Tag
import dev.bakke.artofjuice.TextureAssets
import dev.bakke.artofjuice.engine.AnimationRenderable
import dev.bakke.artofjuice.engine.Entity
import dev.bakke.artofjuice.engine.ParticleSystem
import dev.bakke.artofjuice.engine.collision.ColliderComponent
import dev.bakke.artofjuice.engine.components.Component

class BulletComponent(private val gunStats: GunStats) : Component() {
    override fun lateInit() {
        val colliderComponent = getComponent<ColliderComponent>()
        colliderComponent.onCollision += {
            if (it.hasTag(Tag.ENEMY)) {
                onEnemyHit(it)
            }
        }
        colliderComponent.onTerrainCollision += {
            spawnParticleEffect()
            entity.destroy()
        }
    }

    override fun update(delta: Float) {
        entity.position.x += entity.velocity.x * delta
        entity.position.y += entity.velocity.y * delta
    }

    private fun onEnemyHit(enemy: Entity) {
        // 💡HINT: sånn kan man få tak i en komponent på en annen entity
        enemy.getComponent<HealthComponent>().damage(gunStats.damage)

        // OPPGAVE 2:
        // legg på litt impuls


        spawnParticleEffect()
        entity.destroy()
    }

    private fun spawnParticleEffect() {
        val assets = getSystem<Assets>()
        val animation = assets.getRegions(TextureAssets.Effects.random())
            .let { Animation(1 / 24f, it) }
        getSystem<ParticleSystem>().spawn(
            AnimationRenderable(animation),
            entity.position.cpy(),
            Vector2.Zero.cpy(),
            0.2f
        )
    }
}
