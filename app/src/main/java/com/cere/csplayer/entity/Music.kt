package com.cere.csplayer.entity

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.Collator
import java.util.*

/**
 * Created by CheRevir on 2020/9/5
 */
@Entity(tableName = "music")
data class Music(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "artist") var artist: String,
    @ColumnInfo(name = "album") var album: String,
    @ColumnInfo(name = "duration") var duration: Long,
    @ColumnInfo(name = "star", defaultValue = "0") var star: Int,
    @ColumnInfo(name = "parent") var parent: String
) : Parcelable, Comparable<Music>, Serializable {

    constructor(id: Int) : this(id, "", "", "", 0, 0, "")

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString()!!
    )

    fun getData(): Uri {
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
    }

    override fun compareTo(other: Music): Int {
        return collator.compare(title, other.title)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Music) id == other.id else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeLong(duration)
        parcel.writeInt(star)
        parcel.writeString(parent)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Music(id=$id, title='$title', artist='$artist', album='$album', duration=$duration, star=$star, parent='$parent')"
    }

    companion object CREATOR : Creator<Music> {
        @Ignore
        val collator: Collator = Collator.getInstance(Locale.CHINA)

        override fun createFromParcel(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun newArray(size: Int): Array<Music?> {
            return arrayOfNulls(size)
        }
    }

}