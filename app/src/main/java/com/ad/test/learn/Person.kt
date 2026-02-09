package com.ad.test.learn

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(
    val id: String?,
    val name: String?,
    val age: Int,
) : Parcelable
