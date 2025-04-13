package dev.bakke.artofjuice.Event

class Event {
    private val callbacks: MutableSet<() -> Unit> = mutableSetOf()
    operator fun plusAssign(callback: () -> Unit) {
        callbacks.add(callback)
    }

    operator fun invoke() {
        callbacks.forEach {it.invoke() }
    }
}
