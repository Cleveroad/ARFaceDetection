package com.cleveroad.aropensource.models


interface Model<T> : KParcelable {

    var id: T?
}