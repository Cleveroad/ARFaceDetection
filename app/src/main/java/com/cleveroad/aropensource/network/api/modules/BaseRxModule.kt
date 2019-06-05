package com.cleveroad.aropensource.network.api.modules

import com.cleveroad.aropensource.models.converters.Converter


abstract class BaseRxModule<T, NetworkModel, M>(val api: T, val converter: Converter<NetworkModel, M>)