package com.cleveroad.aropensource.providers.base

import com.cleveroad.aropensource.models.Model
import com.cleveroad.aropensource.repositories.Repository


abstract class BaseProvider<M : Model<*>, NetworkModule, Repo : Repository<M>>
    : BaseOnlineProvider<M, NetworkModule>() {

    val repository: Repo = this.initRepository()

    protected abstract fun initRepository(): Repo
}