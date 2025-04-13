package dev.bakke.artofjuice.Event

class Event2<T1, T2> {
    private val callbacks: MutableSet<(T1, T2) -> Unit> = mutableSetOf()
    operator fun plusAssign(callback: (T1, T2) -> Unit) {
        callbacks.add(callback)
    }

    operator fun invoke(t1: T1, t2: T2) {
        callbacks.forEach {it.invoke(t1, t2) }
    }
}
