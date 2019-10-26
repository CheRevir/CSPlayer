package com.cere.csplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by CheRevir on 2019/10/22
 */
@Table("music")
public class Music implements Comparable<Music>, Parcelable {
    @Ignore
    private static final Collator mCollator = Collator.getInstance(Locale.CHINA);
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    private long id;
    @Column("title")
    private String title;
    @Column("artist")
    private String artist;
    @Column("album")
    private String album;
    @Column("duration")
    private int duration;
    @NotNull
    @Default("0")
    @Column("star")
    private int star;
    @Column("path")
    private String path;

    public Music() {
    }

    protected Music(Parcel parcel) {
        title = parcel.readString();
        artist = parcel.readString();
        album = parcel.readString();
        duration = parcel.readInt();
        star = parcel.readInt();
        path = parcel.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Music)) {
            return false;
        }
        return ((Music) obj).getPath().equals(this.getPath());
    }

    @Override
    public int compareTo(Music o) {
        return mCollator.compare(o.getTitle(), this.getTitle());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeInt(duration);
        dest.writeInt(star);
        dest.writeString(path);
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
