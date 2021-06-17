package com.oladokun.nqb8

import com.google.firebase.database.Exclude

data class UserDetail(
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    @get:Exclude
    var id: String? = null
)
