package com.cleveroad.aropensource.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.cleveroad.aropensource.NPApp
import com.cleveroad.aropensource.utils.EMPTY_STRING
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base class which simplify work with [SharedPreferences]
 *
 * @param key [String] The name of the preference to retrieve
 * @param pref Instance for accessing and modifying preference data returned by [SharedPreferences]
 *
 */
abstract class PreferenceDelegate<T>(private val key: String? = null,
                                     protected val pref: SharedPreferences = NPApp.prefs) : ReadWriteProperty<Any, T?> {
    protected fun getKey(property: KProperty<*>) = key ?: "PREF_${property.name}"

    @SuppressLint("ApplySharedPref")
    protected fun <T> SharedPreferences.checkForRemove(property: KProperty<*>, value: T?,
                                                       block: SharedPreferences.Editor.(key: String, value: T) -> Unit) {
        edit().apply {
            val key = getKey(property)
            value?.let { block(key, it) } ?: remove(key)
        }.commit()
    }
}

/**
 * Class is inherited from [PreferenceDelegate] and use for storing type [String] value
 *
 * @param defaultValue default value [String]
 */
class StringPD(private val defaultValue: String = EMPTY_STRING,
               pref: SharedPreferences = NPApp.prefs,
               key: String? = null) : PreferenceDelegate<String>(key, pref) {

    override fun getValue(thisRef: Any, property: KProperty<*>): String? =
            getKey(property).takeIf { pref.contains(it) }
                    ?.let { pref.getString(it, defaultValue) }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) =
            pref.checkForRemove(property, value) { key, v -> putString(key, v) }
}

/**
 * Class is inherited from [PreferenceDelegate] and use for storing type [Int] value
 *
 * @param defaultValue default value [Int]
 */
class IntPD(private val defaultValue: Int = 0,
            pref: SharedPreferences = NPApp.prefs,
            key: String? = null) : PreferenceDelegate<Int>(key, pref) {

    override fun getValue(thisRef: Any, property: KProperty<*>): Int? =
            getKey(property).takeIf { pref.contains(it) }
                    ?.let { pref.getInt(it, defaultValue) }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int?) =
            pref.checkForRemove(property, value) { key, v -> putInt(key, v) }
}

/**
 * Class is inherited from [PreferenceDelegate] and use for storing type [Long] value
 *
 * @param defaultValue default value [Long]
 */
class LongPD(private val defaultValue: Long = 0,
             pref: SharedPreferences = NPApp.prefs,
             key: String? = null) : PreferenceDelegate<Long>(key, pref) {

    override fun getValue(thisRef: Any, property: KProperty<*>): Long? =
            getKey(property).takeIf { pref.contains(it) }
                    ?.let { pref.getLong(it, defaultValue) }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long?) =
            pref.checkForRemove(property, value) { key, v -> putLong(key, v) }
}

/**
 * Class is inherited from [PreferenceDelegate] and use for storing type [Float] value
 *
 * @param defaultValue default value [Float]
 */
class FloatPD(private val defaultValue: Float = 0F,
              pref: SharedPreferences = NPApp.prefs,
              key: String? = null) : PreferenceDelegate<Float>(key, pref) {

    override fun getValue(thisRef: Any, property: KProperty<*>): Float? =
            getKey(property).takeIf { pref.contains(it) }
                    ?.let { pref.getFloat(it, defaultValue) }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float?) =
            pref.checkForRemove(property, value) { key, v -> putFloat(key, v) }
}

/**
 * Class is inherited from [PreferenceDelegate] and use for storing type [Boolean] value
 *
 * @param defaultValue default value [Boolean]
 */
class BooleanPD(private val defaultValue: Boolean = false,
                pref: SharedPreferences = NPApp.prefs,
                key: String? = null) : PreferenceDelegate<Boolean>(key, pref) {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean? =
            getKey(property).takeIf { pref.contains(it) }
                    ?.let { pref.getBoolean(it, defaultValue) }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean?) =
            pref.checkForRemove(property, value) { key, v -> putBoolean(key, v) }
}