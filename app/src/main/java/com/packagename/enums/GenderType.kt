package com.packagename.enums


enum class GenderType(private val type: Int) {
    FEMALE(0),
    MALE(1),
    ANY(2),
    UNKNOWN(-1);

    operator fun invoke() = type

    companion object {
        fun byValue(value: Int?) = values().firstOrNull { value == it.type } ?: UNKNOWN
    }
}