package dev.bakke.artofjuice.Event

class Event1<T1> {
    private val callbacks: MutableSet<(T1) -> Unit> = mutableSetOf()
    operator fun plusAssign(callback: (T1) -> Unit) {
        callbacks.add(callback)
    }

    operator fun invoke(t1: T1) {
        callbacks.forEach {it.invoke(t1) }
    }
}
