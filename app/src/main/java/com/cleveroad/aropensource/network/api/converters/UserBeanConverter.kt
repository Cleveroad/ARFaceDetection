package com.cleveroad.aropensource.network.api.converters

import com.cleveroad.aropensource.enums.GenderType
import com.cleveroad.aropensource.models.User
import com.cleveroad.aropensource.models.UserModel
import com.cleveroad.aropensource.models.converters.BaseConverter
import com.cleveroad.aropensource.network.api.beans.UserBean

interface UserBeanConverter

class UserBeanConverterImpl : BaseConverter<UserBean, User>(),
    UserBeanConverter {

    override fun processConvertInToOut(inObject: UserBean) = inObject.run {
        UserModel(
            id,
            email,
            firstName,
            lastName,
            avatarUrl,
            phone,
            GenderType.byValue(gender),
            dateOfBirth
        )
    }

    override fun processConvertOutToIn(outObject: User) = outObject.run {
        UserBean(
            id,
            email,
            firstName,
            lastName,
            avatarUrl,
            phone,
            gender?.invoke(),
            dateOfBirth
        )
    }
}
