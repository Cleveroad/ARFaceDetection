package com.cleveroad.aropensource.providers.base

import com.cleveroad.aropensource.models.Model


abstract class BaseOnlineProvider<M : Model<*>, NetworkModule> :
    Provider<M> {

    val networkModule: NetworkModule = this.initNetworkModule()

    protected abstract fun initNetworkModule(): NetworkModule
}