package com.cere.csplayer.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.request.RequestOptions;
import com.cere.csplayer.BuildConfig;
import com.cere.csplayer.Constants;
import com.cere.csplayer.GlideApp;
import com.cere.csplayer.OnPermissionCallback;
import com.cere.csplayer.R;
import com.cere.csplayer.adapter.AlbumArtAdapter;
import com.cere.csplayer.adapter.MusicAdapter;
import com.cere.csplayer.content.MusicManager;
import com.cere.csplayer.control.PlayCallback;
import com.cere.csplayer.control.PlayControl;
import com.cere.csplayer.data.SQLite;
import com.cere.csplayer.data.SharedPre;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;
import com.cere.csplayer.glide.BlurTransformation;
import com.cere.csplayer.service.PlayService;
import com.cere.csplayer.until.BitmapUtils;
import com.cere.csplayer.until.DialogUtils;
import com.cere.csplayer.until.Utils;
import com.cere.csplayer.view.MarqueeTextView;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MusicManager.OnLoadDoneListener, PlayControl.OnConnectedListener, AlbumArtAdapter.OnPageChangeListener {
    private PlayControl mPlayControl;
    private MusicManager mMusicManager;
    private AlbumArtAdapter mAlbumArtAdapter;
    private MusicAdapter mMusicAdapter;
    private Handler mHandler;
    private UpdateUI mUpdateUI;

    private DrawerLayout mDrawerLayout;
    private RatingBar mRatingBar;
    private ImageView iv_background;
    private ImageButton ib_play, ib_repeat, ib_shuffle;
    private SeekBar mSeekBar;
    private TextView tv_time_now, tv_time_max, tv_artist, tv_album, tv_number;
    private MarqueeTextView tv_title;

    private boolean isPlaying = false;
    private boolean isFistRun = true;
    private boolean isAlbumArtSmoothScroll = false;

    private String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        mMusicManager = new MusicManager(this);
        initView();
        mUpdateUI = new UpdateUI();
        initService();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayControl.isConnected()) {
            mPlayControl.unregisterCallback(mPlayCallback.getCallback());
            mPlayControl.disconnect();
        }
    }

    private void initService() {
        mPlayControl = new PlayControl(this, PlayService.class);
        mPlayControl.connect(this);
    }

    @Override
    public void onConnected(boolean isConnected) {
        if (isConnected) {
            mPlayControl.registerCallback(mPlayCallback.getCallback());
            mPlayControl.sendAction(Constants.PLAY_SERVICE_INIT, null);
            mMusicManager.load();
            initData();
        }
    }

    @Override
    public void onMusicLoadDone(ArrayList<Music> list) {
        mMusicAdapter.setList(list);
        if (mPlayControl.isConnected()) {
            mPlayControl.setList(list);
        }
    }

    @Override
    public void onPlayLoadDone(ArrayList<Play> list) {
        mAlbumArtAdapter.setList(list);
        if (mPlayControl.isConnected()) {
            mPlayControl.setPlayList(list);
            mPlayControl.setData(mMusicManager.getMusics().get(mMusicManager.getPlays().get(SharedPre.getInt(this, Constants.PLAY_INDEX, 0)).getPosition()).getPath());
        }
    }

    private void initData() {
        repeat(false);
        shuffle(false);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_bar_home);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.main_drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(toggle);
        BottomAppBar bottomAppBar = findViewById(R.id.main_right_bottom_appbar);
        bottomAppBar.inflateMenu(R.menu.activity_main_menu_bottom_bar);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity.this.onOptionsItemSelected(item);
                return true;
            }
        });
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        });
        iv_background = findViewById(R.id.main_background);
        ib_play = findViewById(R.id.main_ib_play);
        ib_play.setOnClickListener(this);
        ib_repeat = findViewById(R.id.main_ib_repeat);
        ib_repeat.setOnClickListener(this);
        ib_shuffle = findViewById(R.id.main_ib_shuffle);
        ib_shuffle.setOnClickListener(this);
        ib_shuffle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (SharedPre.getBoolean(MainActivity.this, Constants.MODE_SHUFFLE, false)) {
                    mMusicManager.reBuildPlays();
                }
                return true;
            }
        });
        findViewById(R.id.main_ib_prev).setOnClickListener(this);
        findViewById(R.id.main_ib_next).setOnClickListener(this);
        tv_time_now = findViewById(R.id.main_tv_time_now);
        tv_time_max = findViewById(R.id.main_tv_time_max);
        tv_title = findViewById(R.id.main_tv_title);
        tv_artist = findViewById(R.id.main_tv_artist);
        tv_album = findViewById(R.id.main_tv_album);
        tv_number = findViewById(R.id.main_tv_number);
        findViewById(R.id.main_right_bottom_fab).setOnClickListener(this);
        mSeekBar = findViewById(R.id.main_progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayControl.isConnected()) {
                    mPlayControl.seekTo(progress);
                    tv_time_now.setText(Utils.timeToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mRatingBar = findViewById(R.id.main_ratingbar);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    Music music = mMusicManager.getMusic(data);
                    music.setStar((int) rating);
                    SQLite.getLiteOrm(MainActivity.this).update(music);
                }
            }
        });
        mAlbumArtAdapter = new AlbumArtAdapter(this, (ViewPager2) findViewById(R.id.main_viewpager), mMusicManager);
        mAlbumArtAdapter.setOnPageChangeListener(this);
        mMusicAdapter = new MusicAdapter(this, (FastScrollRecyclerView) findViewById(R.id.main_right_recyclerview));
        mMusicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mPlayControl.isConnected()) {
                    if (SharedPre.getBoolean(MainActivity.this, Constants.MODE_SHUFFLE, false)) {
                        MainActivity.this.data = mMusicManager.getMusics().get(position).getPath();
                        mMusicManager.reBuildPlays();
                    } else {
                        mPlayControl.setData(mMusicManager.getMusics().get(mMusicManager.getPlays().get(position).getPosition()).getPath());
                    }
                }
            }
        });
    }

    @Override
    public void onPageSelected(int position) {
        if (mPlayControl.isConnected()) {
            mPlayControl.setData(mMusicManager.getMusics().get(mMusicManager.getPlays().get(position).getPosition()).getPath());
        }
    }

    private PlayCallback mPlayCallback = new PlayCallback() {
        @Override
        public void onPlay(boolean isPlay) {
            isPlaying = isPlay;
            if (isPlay) {
                ib_play.setImageResource(R.drawable.ic_play);
            } else {
                ib_play.setImageResource(R.drawable.ic_pause);
            }
        }

        @Override
        public void onData(String data) {
            MainActivity.this.data = data;
            Music music = mMusicManager.getMusic(data);
            tv_title.setText(music.getTitle());
            tv_artist.setText(music.getArtist());
            tv_album.setText(music.getAlbum());
            mRatingBar.setRating(music.getStar());
            int positionMusics = mMusicManager.getPosition(data);
            mMusicAdapter.setTip(positionMusics);
            if (isFistRun) {
                mMusicAdapter.scrollToTip();
                isFistRun = false;
            }
            int positionPlays = mMusicManager.getPosition(positionMusics);
            tv_number.setText(positionPlays + 1 + "/" + mMusicManager.getMusics().size());
            if (isAlbumArtSmoothScroll) {
                mAlbumArtAdapter.setCurrentItem(positionPlays, true);
                isAlbumArtSmoothScroll = false;
            } else {
                mAlbumArtAdapter.setCurrentItem(positionPlays, false);
            }
            mUpdateUI.setData(data);
            mHandler.post(mUpdateUI);
            SharedPre.putInt(MainActivity.this, Constants.PLAY_INDEX, positionPlays);
        }

        @Override
        public void onDuration(int duration) {
            mSeekBar.setMax(duration);
            tv_time_max.setText(Utils.timeToString(duration));
        }

        @Override
        public void onCurrentDuration(int duration) {
            mSeekBar.setProgress(duration);
            tv_time_now.setText(Utils.timeToString(duration));
        }

        @Override
        public void onAction(String action, Bundle bundle) {
            switch (action) {
                case Constants.PERMISSION_WAKE_LOCK:
                    XXPermissions.with(MainActivity.this).constantRequest().permission(Manifest.permission.WAKE_LOCK).request(new OnPermissionCallback() {
                        @Override
                        public void hasPermission(boolean isAll) {
                            if (mPlayControl.isConnected()) {
                                mPlayControl.sendAction(Constants.PERMISSION_WAKE_LOCK, null);
                            }
                        }
                    });
                    break;
                case Constants.PERMISSION_READ_PHONE_STATE:
                    XXPermissions.with(MainActivity.this).constantRequest().permission(Permission.READ_PHONE_STATE).request(new OnPermissionCallback() {
                        @Override
                        public void hasPermission(boolean isAll) {
                            if (mPlayControl.isConnected()) {
                                mPlayControl.sendAction(Constants.PERMISSION_READ_PHONE_STATE, null);
                            }
                        }
                    });
                    break;
            }
        }
    };

    private class UpdateUI implements Runnable {
        private String data;
        private RequestOptions mRequestOptions;

        public UpdateUI() {
            mRequestOptions = new RequestOptions();
            Bitmap bitmap = BitmapUtils.getBitmapBlur(MainActivity.this, BitmapUtils.getScaleBitmap(BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.clannad), 10, 10), 1.0f);
            mRequestOptions.error(new BitmapDrawable(MainActivity.this.getResources(), bitmap));
        }

        public void setData(String date) {
            this.data = date;
        }

        @Override
        public void run() {
            GlideApp.with(MainActivity.this).asBitmap().apply(mRequestOptions).transform(new BlurTransformation(MainActivity.this)).load(data).into(iv_background);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_ib_play:
                if (mPlayControl.isConnected()) {
                    if (isPlaying) {
                        mPlayControl.pause();
                    } else {
                        mPlayControl.play();
                    }
                }
                break;
            case R.id.main_ib_prev:
                if (mPlayControl.isConnected()) {
                    mPlayControl.previous();
                    isAlbumArtSmoothScroll = true;
                }
                break;
            case R.id.main_ib_next:
                if (mPlayControl.isConnected()) {
                    mPlayControl.next();
                    isAlbumArtSmoothScroll = true;
                }
                break;
            case R.id.main_ib_repeat:
                if (mPlayControl.isConnected()) {
                    repeat(true);
                }
                break;
            case R.id.main_ib_shuffle:
                if (mPlayControl.isConnected()) {
                    shuffle(true);
                }
                break;
            case R.id.main_right_bottom_fab:
                mMusicAdapter.smoothScrollToTip();
                break;
        }
    }

    private void repeat(boolean isClick) {
        int mode = SharedPre.getInt(this, Constants.MODE_REPEAT, 0);
        if (isClick) {
            mode++;
            if (mode > 2) {
                mode = 0;
            }
            SharedPre.putInt(this, Constants.MODE_REPEAT, mode);
        }
        switch (mode) {
            case 0:
                ib_repeat.setImageResource(R.drawable.ic_repeat_none);
                break;
            case 1:
                ib_repeat.setImageResource(R.drawable.ic_repeat_single);
                break;
            case 2:
                ib_repeat.setImageResource(R.drawable.ic_repeat_all);
                break;
        }
        if (mPlayControl.isConnected()) {
            mPlayControl.setRepeatMode(mode);
        }
    }

    private void shuffle(boolean isClick) {
        boolean mode = SharedPre.getBoolean(this, Constants.MODE_SHUFFLE, false);
        if (isClick) {
            mode = !mode;
            SharedPre.putBoolean(this, Constants.MODE_SHUFFLE, mode);
            mMusicManager.reBuildPlays();
        }
        if (mode) {
            ib_shuffle.setImageResource(R.drawable.ic_shuffle_all);
        } else {
            ib_shuffle.setImageResource(R.drawable.ic_shuffle_none);
        }
        if (mPlayControl.isConnected()) {
            mPlayControl.setShuffleModeEnabled(mode);
        }
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_setting:
                break;
            case R.id.menu_main_list:
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
                break;
            case R.id.menu_main_scan:
                mMusicManager.reScanMusics();
                break;
            case R.id.menu_main_about:
                about();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                moveTaskToBack(false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void about() {
        DialogUtils.getAlertDialog(this, "关于", "版本号：" + BuildConfig.VERSION_CODE + "\n\n版本名：" + BuildConfig.VERSION_NAME + "\n\n开发者：" + BuildConfig.DEVELOPER, R.mipmap.ic_launcher)
                .setPositiveButton("确定", null)
                .show();
    }
}
