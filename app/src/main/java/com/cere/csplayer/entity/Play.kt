package com.cere.csplayer.entity

import android.os.Parcel
import android.os.Parcelable
import com.litesuits.orm.db.annotation.Column
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.annotation.Table
import com.litesuits.orm.db.enums.AssignType

/**
 * Created by CheRevir on 2019/10/21
 */
@Table("play")
open class Play : Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    private val id: Long = 0

    @Column("position")
    var position = 0

    constructor() {}
    protected constructor(`in`: Parcel) {
        position = `in`.readInt()
    }

    override fun hashCode(): Int {
        return position
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Play) {
            false
        } else other.position == position
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(position)
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