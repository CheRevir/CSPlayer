package com.cere.csplayer.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by CheRevir on 2020/9/5
 */
@Entity(tableName = "play")
data class Play(
    @ColumnInfo(name = "id") var id: Int,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") var ID: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readInt())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Play) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ID)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Play(_id=$ID, id=$id)"
    }

    companion object CREATOR : Parcelable.Creator<Play> {
        override fun createFromParcel(parcel: Parcel): Play {
            return Play(parcel)
        }

        override fun newArray(size: Int): Array<Play?> {
            return arrayOfNulls(size)
        }
    }
}