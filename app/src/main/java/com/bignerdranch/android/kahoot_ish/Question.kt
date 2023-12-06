package com.bignerdranch.android.kahoot_ish

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val answer: String = "",
    val incorrect: String = "",
    val question: String = ""
) : Parcelable
