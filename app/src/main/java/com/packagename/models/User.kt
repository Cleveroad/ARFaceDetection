package com.packagename.models

import android.os.Parcel
import com.packagename.enums.GenderType
import com.packagename.utils.EMPTY_STRING
import org.joda.time.DateTime


interface User : Model<Long> {
    val email: String?
    val firstName: String?
    val lastName: String?
    val avatarUrl: String?
    val phone: String?
    val gender: GenderType?
    val dateOfBirth: DateTime?

    fun getFullName() = "${firstName ?: EMPTY_STRING} ${lastName ?: EMPTY_STRING}"
}

class UserModel(override var id: Long? = null,
                override var email: String? = EMPTY_STRING,
                override var firstName: String? = EMPTY_STRING,
                override var lastName: String? = EMPTY_STRING,
                override var avatarUrl: String? = EMPTY_STRING,
                override var phone: String? = EMPTY_STRING,
                override var gender: GenderType? = GenderType.UNKNOWN,
                override var dateOfBirth: DateTime? = null) : User {
    companion object {
        @JvmField
        val CREATOR = KParcelable.generateCreator {
            UserModel(it.read(), it.read(), it.read(), it.read(),
                    it.read(), it.read(), it.read(), it.read())
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) =
            dest.write(id, email, firstName, lastName, avatarUrl, phone, gender, dateOfBirth)
}