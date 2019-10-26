package com.cere.csplayer.content;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import com.cere.csplayer.R;
import com.cere.csplayer.activity.MainActivity;
import com.cere.csplayer.data.SQLite;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;
import com.cere.csplayer.until.DialogUtils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CheRevir on 2019/10/22
 */
public class MusicManager implements MusicScanner.OnScanDoneListener, PlayBuilder.OnBuilderDonListener {
    private Context mContext;
    private Handler mHandler;
    private OnLoadDoneListener mOnLoadDoneListener;
    private ArrayList<Music> mMusics;
    private ArrayList<Play> mPlays;

    public MusicManager(MainActivity activity) {
        mContext = activity;
        mOnLoadDoneListener = activity;
        mHandler = new Handler(mContext.getMainLooper());
    }

    public void load() {
        String[] permissions = new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.SYSTEM_ALERT_WINDOW};
        if (XXPermissions.isHasPermission(mContext, permissions)) {
            mMusics = SQLite.getLiteOrm(mContext).query(Music.class);
            if (!mMusics.isEmpty()) {
                mPlays = SQLite.getLiteOrm(mContext).query(Play.class);
                mOnLoadDoneListener.onMusicLoadDone(mMusics);
                if (!mPlays.isEmpty()) {
                    mOnLoadDoneListener.onPlayLoadDone(mPlays);
                } else {
                    reBuildPlays();
                }
            } else {
                reScanMusics();
            }
        } else {
            XXPermissions.with((MainActivity) mContext).constantRequest().permission(permissions).request(new OnPermission() {
                @Override
                public void hasPermission(List<String> granted, boolean isAll) {
                    if (isAll) {
                        load();
                    }
                }

                @Override
                public void noPermission(List<String> denied, boolean quick) {
                    DialogUtils.getAlertDialog(mContext, "提示", "没有权限将无法扫描音乐文件。", R.mipmap.ic_launcher)
                            .setPositiveButton("去设置打开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    XXPermissions.gotoPermissionSettings(mContext);
                                }
                            }).show();
                }
            });
        }
    }

    @Override
    public void onScanDone(ArrayList<Music> list) {
        if (!list.isEmpty()) {
            this.mMusics = list;
            mOnLoadDoneListener.onMusicLoadDone(list);
            reBuildPlays();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SQLite.getLiteOrm(mContext).delete(Music.class);
                    SQLite.getLiteOrm(mContext).save(mMusics);
                }
            });
        } else {
            DialogUtils.getAlertDialog(mContext, "提示", "没有扫描到音乐文件。", R.mipmap.ic_launcher).setPositiveButton("确定", null).show();
        }
    }

    @Override
    public void onBuildDone(final ArrayList<Play> list) {
        this.mPlays = list;
        mOnLoadDoneListener.onPlayLoadDone(list);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                SQLite.getLiteOrm(mContext).delete(Play.class);
                SQLite.getLiteOrm(mContext).save(list);
            }
        });
    }

    public void reScanMusics() {
        new MusicScanner(mContext, this).execute();
    }

    public void reBuildPlays() {
        Play play = new Play();
        String data = ((MainActivity) mContext).getData();
        if (mPlays != null && !data.isEmpty()) {
            play.setPosition(getPosition(data));
        } else {
            play.setPosition(0);
        }
        new PlayBuilder(mContext, this, play).execute(mMusics);
    }

    public void deleteMusic(final String path) {
        Music music = new Music();
        music.setPath(path);
        mMusics.remove(music);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                WhereBuilder builder = new WhereBuilder(Music.class);
                builder.equals("path", path);
                SQLite.getLiteOrm(mContext).delete(builder);
            }
        });
    }

    public void deletePlay(final int position) {
        Play play = new Play();
        play.setPosition(position);
        mPlays.remove(position);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                WhereBuilder builder = new WhereBuilder(Play.class);
                builder.equals("position", position);
                SQLite.getLiteOrm(mContext).delete(builder);
            }
        });
    }

    public int getPosition(String path) {
        Music music = new Music();
        music.setPath(path);
        return mMusics.indexOf(music);
    }

    public int getPosition(int position) {
        Play play = new Play();
        play.setPosition(position);
        return mPlays.indexOf(play);
    }

    public Music getMusic(String path) {
        return mMusics.get(getPosition(path));
    }

    public Play getPlay(int position) {
        return mPlays.get(position);
    }

    public ArrayList<Music> getMusics() {
        return mMusics;
    }

    public ArrayList<Play> getPlays() {
        return mPlays;
    }

    public interface OnLoadDoneListener {
        void onMusicLoadDone(ArrayList<Music> list);

        void onPlayLoadDone(ArrayList<Play> list);
    }
}
