package com.thurainx.matchguard.data.vos

import java.util.*

data class DateVO(
    val rawDate: String?,
    val formatDate: String?,
    val standardMatchDate: String?,
    val day: String?,
    var isSelected: Boolean = false

)