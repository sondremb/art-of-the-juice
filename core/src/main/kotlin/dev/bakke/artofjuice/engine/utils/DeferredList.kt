package dev.bakke.artofjuice.engine.utils

class DeferredList<T> {
    private val internalItems = mutableListOf<T>()
    private val itemsToAdd = mutableSetOf<T>()
    private val itemsToRemove = mutableSetOf<T>()
    val items: List<T>
        get() = internalItems

    fun add(item: T) {
        itemsToAdd.add(item)
    }

    fun remove(item: T) {
        itemsToAdd.remove(item)
        itemsToRemove.add(item)
    }

    private fun addNewItems(beforeAdd: ((T) -> Unit)? = null) {
        beforeAdd?.let { func ->
            itemsToAdd.forEach { func.invoke(it) }
        }
        internalItems.addAll(itemsToAdd)
        itemsToAdd.clear()
    }

    private fun removeOldItems(beforeRemove: ((T) -> Unit)? = null) {
        beforeRemove?.let { func ->
            itemsToRemove.forEach { func.invoke(it) }
        }
        internalItems.removeAll(itemsToRemove)
        itemsToRemove.clear()
    }


    fun update(
        beforeAdd: ((T) -> Unit)? = null,
        beforeRemove: ((T) -> Unit)? = null
    ) {
        removeOldItems(beforeRemove)
        addNewItems(beforeAdd)
    }
}
