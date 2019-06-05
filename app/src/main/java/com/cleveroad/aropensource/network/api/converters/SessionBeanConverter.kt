package com.cleveroad.aropensource.network.api.converters

import com.cleveroad.aropensource.models.Session
import com.cleveroad.aropensource.models.SessionModel
import com.cleveroad.aropensource.models.converters.BaseConverter
import com.cleveroad.aropensource.network.api.beans.SessionBean

interface SessionBeanConverter

class SessionBeanConverterImpl : BaseConverter<SessionBean, Session>(),
    SessionBeanConverter {

    override fun processConvertInToOut(inObject: SessionBean) = inObject.run {
        SessionModel(accessToken, refreshToken, expiresAt)
    }

    override fun processConvertOutToIn(outObject: Session) = outObject.run {
        SessionBean(accessToken, refreshToken, expireDate)
    }
}
