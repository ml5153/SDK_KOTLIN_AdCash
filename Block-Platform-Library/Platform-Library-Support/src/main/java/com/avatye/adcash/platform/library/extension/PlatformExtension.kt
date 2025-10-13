package com.avatye.adcash.platform.library.extension

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.net.URLEncoder

inline fun SharedPreferences.editor(action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)
    editor.apply()
}

inline fun <R> TypedArray.useRecycle(block: (TypedArray) -> R): R {
    return block(this).also {
        recycle()
    }
}

inline val String.toBase64: String
    get() {
        return try {
            Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }

inline val String.toUrlEncode: String
    get() {
        return try {
            URLEncoder.encode(this, "UTF-8")
        } catch (e: Exception) {
            ""
        }
    }

inline val Int.toPX: Float
    get() {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f)
    }

inline val Float.toPX: Float
    get() {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f)
    }

inline val Int.toDP: Float
    get() {
        return (this / Resources.getSystem().displayMetrics.density)
    }

inline val Float.toDP: Float
    get() {
        return (this / Resources.getSystem().displayMetrics.density)
    }

inline fun <T> T.produce(block: (T) -> Unit) {
    block(this)
}

inline fun <T1 : Any, T2 : Any> produce2(p1: T1?, p2: T2?, block: (T1, T2) -> Unit) {
    if (p1 != null && p2 != null) {
        block(p1, p2)
    }
}

inline fun <T1 : Any, T2 : Any, T3 : Any> produce3(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> Unit) {
    if (p1 != null && p2 != null && p3 != null) {
        block(p1, p2, p3)
    }
}

inline fun <T1 : Any, T2 : Any, R : Any> let2(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> let3(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

// region { JSON }
@Throws(Exception::class)
fun JSONObject.toStringValue(name: String, default: String = ""): String {
    return when {
        this.has(name) && !this.isNull(name) -> this.getString(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toStringValue(firstName: String, secondName: String, default: String = ""): String {
    return if (this.has(firstName) && !this.isNull(firstName)) {
        this.getString(firstName)
    } else if (this.has(secondName) && !this.isNull(secondName)) {
        this.getString(secondName)
    } else {
        default
    }
}

fun JSONObject.toIntValue(name: String, default: Int = 0): Int {
    return try {
        when {
            this.has(name) && !this.isNull(name) -> this.getInt(name)
            else -> default
        }
    } catch (e: Exception) {
        println("JSONObject.toIntValue in ADCash '${e.message}'")
        default
    }
}

fun JSONObject.toFloatValue(name: String, default: Float = 0f): Float {
    return try {
        when {
            this.has(name) && !this.isNull(name) -> this.getDouble(name).toFloat()
            else -> default
        }
    } catch (e: Exception) {
        println("JSONObject.toFloatValue in ADCash '${e.message}'")
        default
    }
}

fun JSONObject.toLongValue(name: String, default: Long = 0L): Long {
    return try {
        when {
            this.has(name) && !this.isNull(name) -> this.getLong(name)
            else -> default
        }
    } catch (e: Exception) {
        println("JSONObject.toLongValue in ADCash '${e.message}'")
        default
    }
}

fun JSONObject.toDoubleValue(name: String, default: Double = 0.0): Double {
    return try {
        when {
            this.has(name) && !this.isNull(name) -> this.getDouble(name)
            else -> default
        }
    } catch (e: Exception) {
        println("JSONObject.toDoubleValue in ADCash '${e.message}'")
        default
    }
}

@Throws(Exception::class)
fun JSONObject.toBooleanValue(name: String, default: Boolean = false): Boolean {
    return when {
        this.has(name) && !this.isNull(name) -> {
            val innerValue = this.getString(name)
            when (innerValue.lowercase()) {
                "1", "true" -> true
                "0", "false" -> false
                else -> default
            }
        }

        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toJSONObjectValue(name: String): JSONObject? {
    return when {
        this.has(name) && !this.isNull(name) -> this.getJSONObject(name)
        else -> null
    }
}

@Throws(Exception::class)
fun JSONObject.toJSONArrayValue(name: String): JSONArray? {
    return when {
        this.has(name) && !this.isNull(name) -> this.getJSONArray(name)
        else -> null
    }
}

fun JSONArray.isEmpty(): Boolean {
    return this.length() == 0
}

fun JSONArray.until(loop: (json: JSONObject) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(this.getJSONObject(i))
    }
}

fun JSONArray.untilAny(loop: (i: Any) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(this.get(i))
    }
}

fun JSONArray.until(feasibility: (feasible: Boolean) -> Unit, loop: (json: JSONObject) -> Unit) {
    // size
    val length = this.length()
    // call -> feasibility
    feasibility(length > 0)
    // call -> loop
    for (i in 0 until length) {
        loop(this.getJSONObject(i))
    }
}

fun JSONArray.untilWithIndex(loop: (index: Int, json: JSONObject) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(i, this.getJSONObject(i))
    }
}
// endregion

val Activity?.isAlive: Boolean
    get() = !(this?.isFinishing ?: true)

@JvmName("isAvailableFromActivity")
inline fun WeakReference<Activity>?.isAvailable(block: (weakActivity: Activity) -> Unit) {
    this?.get()?.produce {
        if (it.isAlive) {
            block(it)
        }
    }
}

@JvmName("isAvailableFromContext")
inline fun WeakReference<Context>?.isAvailable(block: (weakContext: Context) -> Unit) {
    this?.get()?.produce {
        block(it)
    }
}