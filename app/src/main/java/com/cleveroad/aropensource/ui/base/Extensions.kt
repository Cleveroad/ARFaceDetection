package com.cleveroad.aropensource.ui.base

/**
 * Bind interface
 *
 * @param objects vararg of [Any] which can implement interface
 *
 * @return first object which implement interface [T]
 */
inline fun <reified T> bindInterfaceOrThrow(vararg objects: Any?): T =
        objects.find { it is T } as T
                ?: throw NotImplementedInterfaceException(T::class.java)