package com.cere.csplayer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2020/9/5
 */
@Dao
interface PlayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(play: Play)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<Play>)

    @Delete
    suspend fun delete(play: Play): Int

    @Delete
    suspend fun delete(list: List<Play>): Int

    @Query("delete from play")
    suspend fun deleteAll(): Int

    @Update
    suspend fun update(play: Play)

    @Query("select * from play")
    fun queryAll(): LiveData<List<Play>>

    @Query("select * from play")
    suspend fun query(): List<Play>
}