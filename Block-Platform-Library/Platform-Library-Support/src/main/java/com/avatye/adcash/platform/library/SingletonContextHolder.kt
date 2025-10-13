package com.avatye.adcash.platform.library

import android.content.Context

open class SingletonContextHolder<out T : Any>(creator: (context: Context) -> T) {

    private var creator: ((Context) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun instance(context: Context): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(context.applicationContext)
                instance = created
                creator = null
                created
            }
        }
    }
}