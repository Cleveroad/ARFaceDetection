package com.cleveroad.aropensource.ui.screens.auth.sign_up

import com.cleveroad.aropensource.ui.screens.info.TypeInfo

interface SignUpCallback {

    fun openSignIn()

    fun openInfoScreen(type: TypeInfo)

}