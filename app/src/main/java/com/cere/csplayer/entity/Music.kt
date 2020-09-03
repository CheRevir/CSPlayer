package com.cere.csplayer.entity

import android.os.Parcel
import android.os.Parcelable
import com.litesuits.orm.db.annotation.*
import com.litesuits.orm.db.enums.AssignType
import java.text.Collator
import java.util.*

/**
 * Created by CheRevir on 2019/10/22
 */
@Table("music")
open class Music : Comparable<Music>, Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    private val id: Long = 0

    @Column("title")
    var title: String? = null

    @Column("artist")
    var artist: String? = null

    @Column("album")
    var album: String? = null

    @Column("duration")
    var duration = 0

    @NotNull
    @Default("0")
    @Column("star")
    var star = 0

    @NotNull
    @Column("path")
    var path: String? = null

    constructor() {}
    protected constructor(parcel: Parcel) {
        title = parcel.readString()
        artist = parcel.readString()
        album = parcel.readString()
        duration = parcel.readInt()
        star = parcel.readInt()
        path = parcel.readString()
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Music) {
            false
        } else other.path == path
    }

    override fun compareTo(other: Music): Int {
        return mCollator.compare(other.title, title)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(artist)
        dest.writeString(album)
        dest.writeInt(duration)
        dest.writeInt(star)
        dest.writeString(path)
    }

    companion object CREATOR : Parcelable.Creator<Music> {
        @Ignore
        private val mCollator = Collator.getInstance(Locale.CHINA)

        override fun createFromParcel(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun newArray(size: Int): Array<Music?> {
            return arrayOfNulls(size)
        }
    }
}