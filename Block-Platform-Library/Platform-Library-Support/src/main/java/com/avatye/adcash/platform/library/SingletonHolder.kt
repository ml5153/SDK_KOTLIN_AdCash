package com.avatye.adcash.platform.library

open class SingletonHolder<out T : Any>(creator: () -> T) {

    private var creator: (() -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun instance(): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!()
                instance = created
                creator = null
                created
            }
        }
    }
}