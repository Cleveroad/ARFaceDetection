package com.cleveroad.aropensource.utils

import com.cleveroad.bootstrap.kotlin_core.ui.NotImplementedInterfaceException


inline fun <reified T> bindInterfaceOrThrow(vararg objects: Any?): T = objects.find { it is T }
        ?.let { it as T }
        ?: throw NotImplementedInterfaceException(T::class.java)