package com.thurainx.matchguard.persistence.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thurainx.matchguard.data.vos.MatchVO

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatch(matchVO: MatchVO)

    @Query("SELECT * FROM matches")
    fun getAllMatches():List<MatchVO>?

    @Query("SELECT id FROM matches")
    fun getMatchIdList(): List<String>?

    @Query("SELECT * FROM matches WHERE id = :id")
    fun getMatchOrNull(id: String): MatchVO?

    @Query("DELETE FROM matches")
    fun deleteAllMatches()

    @Query("DELETE FROM matches WHERE id = :id")
    fun deleteMatchById(id: String)
}