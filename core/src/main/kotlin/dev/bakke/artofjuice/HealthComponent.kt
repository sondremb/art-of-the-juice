package dev.bakke.artofjuice

import dev.bakke.artofjuice.engine.Event.Event
import dev.bakke.artofjuice.engine.Event.Event1
import dev.bakke.artofjuice.engine.components.Component

class HealthComponent(val maxHealth: Int) : Component() {
    var health = maxHealth
        private set

    val onDeath = Event()
    val onDamage = Event1<Int>()

    fun damage(amount: Int) {
        health = (health - amount).coerceAtLeast(0)
        onDamage.invoke(amount)
        if (health <= 0) {
            onDeath.invoke()
        }
    }
}
