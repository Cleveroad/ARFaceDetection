package com.packagename.network.api.converters

import com.packagename.enums.GenderType
import com.packagename.models.User
import com.packagename.models.UserModel
import com.packagename.models.converters.BaseConverter
import com.packagename.network.api.beans.UserBean

interface UserBeanConverter

class UserBeanConverterImpl : BaseConverter<UserBean, User>(), UserBeanConverter {

    override fun processConvertInToOut(inObject: UserBean) = inObject.run {
        UserModel(id, email, firstName, lastName, avatarUrl, phone, GenderType.byValue(gender), dateOfBirth)
    }

    override fun processConvertOutToIn(outObject: User) = outObject.run {
        UserBean(id, email, firstName, lastName, avatarUrl, phone, gender?.invoke(), dateOfBirth)
    }
}
