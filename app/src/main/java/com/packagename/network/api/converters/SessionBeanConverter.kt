package com.packagename.network.api.converters

import com.packagename.models.Session
import com.packagename.models.SessionModel
import com.packagename.models.converters.BaseConverter
import com.packagename.network.api.beans.SessionBean

interface SessionBeanConverter

class SessionBeanConverterImpl : BaseConverter<SessionBean, Session>(), SessionBeanConverter {

    override fun processConvertInToOut(inObject: SessionBean) = inObject.run {
        SessionModel(accessToken, refreshToken, expiresAt)
    }

    override fun processConvertOutToIn(outObject: Session) = outObject.run {
        SessionBean(accessToken, refreshToken, expireDate)
    }
}
