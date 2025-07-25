package com.nanda.pertemuan12tugas2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import com.nanda.pertemuan12tugas2.Friend

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends")
    fun getAllFriends(): List<Friend> // Hapus suspend

    @Insert
    fun insert(friend: Friend) // Hapus suspend

    @Update
    fun update(friend: Friend) // Hapus suspend

    @Delete
    fun delete(friend: Friend) // Hapus suspend
}