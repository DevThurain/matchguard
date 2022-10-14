package com.thurainx.matchguard.data.vos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "matches")
data class MatchVO(
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "leftTeamName")
    val leftTeamName: String?,

    @ColumnInfo(name = "leftTeamImageUrl")
    val leftTeamImageUrl: String?,

    @ColumnInfo(name = "rightTeamName")
    val rightTeamName: String?,

    @ColumnInfo(name = "rightTeamImageUrl")
    val rightTeamImageUrl: String?,

    @ColumnInfo(name = "unixTime")
    val unixTime: String?,

    @ColumnInfo(name = "leagueName")
    val leagueName: String?,

    @ColumnInfo(name = "score")
    val score: String?,

    @ColumnInfo(name = "subscribeNoti")
    var subscribeNoti: Boolean = false,

    @ColumnInfo(name = "alreadyShow")
    var alreadyShow: Boolean = false,

    @ColumnInfo(name = "standardMatchDate")
    val standardMatchDate: String?,

    @ColumnInfo(name = "uniqueId")
    var uniqueId: Int = 0
)