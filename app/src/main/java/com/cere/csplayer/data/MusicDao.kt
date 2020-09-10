package com.cere.csplayer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cere.csplayer.entity.Music

/**
 * Created by CheRevir on 2020/9/5
 */
@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(music: Music)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: List<Music>)

    @Delete
    suspend fun delete(music: Music): Int

    @Delete
    suspend fun delete(list: List<Music>): Int

    @Query("delete from music")
    suspend fun deleteAll(): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(music: Music): Int

    @Query("select * from music")
    fun queryAll(): LiveData<List<Music>>

    @Query("select * from music")
    suspend fun query(): List<Music>

    @Query("select * from music where id=:id")
    suspend fun query(id: Int): Music?
}