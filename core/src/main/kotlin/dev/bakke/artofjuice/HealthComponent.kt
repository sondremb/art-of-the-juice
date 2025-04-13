package dev.bakke.artofjuice

import dev.bakke.artofjuice.Event.Event
import dev.bakke.artofjuice.Event.Event1
import dev.bakke.artofjuice.components.Component

class HealthComponent(val maxHealth: Int) : Component() {
    var health = maxHealth
        private set

    val onDeath = Event()
    val onDamage = Event1<Int>()

    override fun update(delta: Float) {
        if (health <= 0) {
            onDeath.invoke()
            entity.destroy()
        }
    }

    fun damage(amount: Int) {
        health = (health - amount).coerceAtLeast(0)
        onDamage.invoke(amount)
    }
}
