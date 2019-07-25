package com.cleveroad.aropensource.ui.base

/**
 * Exception for cases when someone trying to use instances of class without interface implementation
 */
class NotImplementedInterfaceException(clazz: Class<*>) : ClassCastException(String.format(MESSAGE, clazz)) {

    companion object {
        private const val MESSAGE = "You need to implement %s"
    }
}