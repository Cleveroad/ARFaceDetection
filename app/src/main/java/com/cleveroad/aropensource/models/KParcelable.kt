package com.cleveroad.aropensource.models

import android.os.Parcel
import android.os.Parcelable

interface KParcelable : Parcelable {
    override fun describeContents(): Int = 0

    companion object {
        inline fun <reified T : Any> generateCreator(crossinline create: (source: Parcel) -> T): Parcelable.Creator<T> = object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T = create(source)

            override fun newArray(size: Int): Array<out T?> = arrayOfNulls<T>(size)
        }
    }
}

inline fun <reified T> Parcel.read(): T = readValue(T::class.javaClass.classLoader) as T
fun Parcel.write(vararg values: Any?) = values.forEach { writeValue(it) }
