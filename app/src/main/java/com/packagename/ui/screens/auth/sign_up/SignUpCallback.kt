package com.packagename.ui.screens.auth.sign_up

import com.packagename.ui.screens.info.TypeInfo

interface SignUpCallback {

    fun openSignIn()

    fun openInfoScreen(type: TypeInfo)

}