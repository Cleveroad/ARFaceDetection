package com.packagename.providers.base

import com.packagename.models.Model
import com.packagename.repositories.Repository


abstract class BaseProvider<M : Model<*>, NetworkModule, Repo : Repository<M>>
    : BaseOnlineProvider<M, NetworkModule>() {

    val repository: Repo = this.initRepository()

    protected abstract fun initRepository(): Repo
}