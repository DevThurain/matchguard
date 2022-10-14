package com.thurainx.matchguard.delegate

import com.thurainx.matchguard.data.vos.DateVO
import com.thurainx.matchguard.data.vos.MatchVO


interface NotiDelegate {
    fun onTapNoti(match: MatchVO)
}