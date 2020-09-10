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
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "position") var position: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun equals(other: Any?): Boolean {
        return if (other is Play) position == other.position else false
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
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