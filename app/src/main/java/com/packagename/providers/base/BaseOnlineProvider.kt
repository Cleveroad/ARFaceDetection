package com.packagename.providers.base

import com.packagename.models.Model


abstract class BaseOnlineProvider<M : Model<*>, NetworkModule> : Provider<M> {

    val networkModule: NetworkModule = this.initNetworkModule()

    protected abstract fun initNetworkModule(): NetworkModule
}