package com.cleveroad.aropensource.models

import android.os.Parcel
import com.cleveroad.aropensource.utils.NOTHING
import org.joda.time.DateTime

interface Session : KParcelable {
    var accessToken: String?
    var refreshToken: String?
    var expireDate: DateTime?
    var type: String?

    fun newSession(accessToken: String?, refreshToken: String?, expireDate: DateTime?, type: String?) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.expireDate = expireDate
        this.type = type
    }

    fun clear() {
        this.accessToken = NOTHING
        this.refreshToken = NOTHING
        this.expireDate = NOTHING
        this.type = NOTHING
    }
}

class SessionModel(override var accessToken: String? = NOTHING,
                   override var refreshToken: String? = NOTHING,
                   override var expireDate: DateTime? = NOTHING,
                   override var type: String? = NOTHING
) : Session {
    companion object {
        @JvmField
        val CREATOR = KParcelable.generateCreator {
            SessionModel(it.read(), it.read(), it.read(), it.read())
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) =
            dest.write(accessToken, refreshToken, expireDate, type)
}
