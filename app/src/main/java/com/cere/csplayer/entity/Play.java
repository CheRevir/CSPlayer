package com.cere.csplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by CheRevir on 2019/10/21
 */
@Table("play")
public class Play implements Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    private long id;
    @Column("position")
    private int position;
    
    public Play() {
    }

    protected Play(Parcel in) {
        position = in.readInt();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        return position;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Play)) {
            return false;
        }
        return ((Play) obj).getPosition() == this.getPosition();
    }

    public static final Creator<Play> CREATOR = new Creator<Play>() {
        @Override
        public Play createFromParcel(Parcel in) {
            return new Play(in);
        }

        @Override
        public Play[] newArray(int size) {
            return new Play[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
    }
}
