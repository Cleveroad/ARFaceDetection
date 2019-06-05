package com.packagename.ui.base

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.Companion.getExtra
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Class which simplify work with [Fragment] arguments
 */
class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T?> {

    private var value: T? = null

    /**
     * Returns the value of the property for the given object.
     *
     * @param thisRef the object [Fragment] for which the value is requested.
     * @param property the metadata for the property [KProperty].
     *
     * @return the property value [T].
     */
    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T? = value
            ?: (thisRef.arguments?.get(getExtra(property.name, thisRef::class.java)) as? T)
                    .also { value = it }

    /**
     * Sets the value of the property for the given object.
     *
     * @param thisRef the object [Fragment] for which the value is requested.
     * @param property the metadata for the property [KProperty].
     * @param value the value [T] to set.
     */
    @Suppress("UNCHECKED_CAST")
    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
        value?.let {
            val args = thisRef.arguments ?: Bundle().apply { thisRef.arguments = this }
            val key = getExtra(property.name, thisRef::class.java)

            when (it) {
                is Int -> args.putInt(key, it)
                is Long -> args.putLong(key, it)
                is CharSequence -> args.putCharSequence(key, it)
                is String -> args.putString(key, it)
                is Float -> args.putFloat(key, it)
                is Double -> args.putDouble(key, it)
                is Char -> args.putChar(key, it)
                is Short -> args.putShort(key, it)
                is Boolean -> args.putBoolean(key, it)
                is Serializable -> args.putSerializable(key, it)
                is Bundle -> args.putBundle(key, it)
                is Parcelable -> args.putParcelable(key, it)
                is Array<*> -> when {
                    it.isArrayOf<CharSequence>() -> args.putCharSequenceArray(key, it as Array<CharSequence>)
                    it.isArrayOf<String>() -> args.putStringArray(key, it as Array<String>)
                    it.isArrayOf<Parcelable>() -> args.putParcelableArray(key, it as Array<Parcelable>)
                    else -> propertyNotSupport(it, property)
                }
                is IntArray -> args.putIntArray(key, it)
                is LongArray -> args.putLongArray(key, it)
                is FloatArray -> args.putFloatArray(key, it)
                is DoubleArray -> args.putDoubleArray(key, it)
                is CharArray -> args.putCharArray(key, it)
                is ShortArray -> args.putShortArray(key, it)
                is BooleanArray -> args.putBooleanArray(key, it)
                else -> propertyNotSupport(it, property)
            }
        }
    }

    private fun propertyNotSupport(it: T, property: KProperty<*>): Nothing =
            throw IllegalStateException("Type ${it.javaClass.canonicalName} of property ${property.name} is not supported")
}
