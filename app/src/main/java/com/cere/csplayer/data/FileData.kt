package com.cere.csplayer.data

import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable

/**
 * Created by CheRevir on 2020/9/8
 */
data class FileData(
    var id: Int = 0,
    var exists: Boolean = false,
    var fd: ParcelFileDescriptor? = null
) :
    Parcelable {

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        exists = parcel.readByte() != 0.toByte()
        fd = parcel.readParcelable(ParcelFileDescriptor::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByte(if (exists) 1 else 0)
        parcel.writeParcelable(fd, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "FileData(id=$id, exists=$exists, fd=$fd)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is FileData) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    companion object CREATOR : Parcelable.Creator<FileData> {
        override fun createFromParcel(parcel: Parcel): FileData {
            return FileData(parcel)
        }

        override fun newArray(size: Int): Array<FileData?> {
            return arrayOfNulls(size)
        }
    }
}