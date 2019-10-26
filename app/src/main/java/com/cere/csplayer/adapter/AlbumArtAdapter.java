package com.cere.csplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.cere.csplayer.R;
import com.cere.csplayer.content.MusicManager;
import com.cere.csplayer.entity.Play;
import com.cere.csplayer.until.MusicMetadataRetriever;
import com.cere.csplayer.view.PageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CheRevir on 2019/10/21
 */
public class AlbumArtAdapter extends RecyclerView.Adapter<AlbumArtAdapter.ViewHolder> {
    private ViewPager2 mViewPager2;
    private Context mContext;
    private AsyncListDiffer<Play> mDiffer;
    private MusicManager mMusicManager;
    private OnPageChangeCallback mOnPageChangeCallback;
    private OnPageChangeListener mOnPageChangeListener;
    private boolean isManualScroll = false;

    public AlbumArtAdapter(Context context, ViewPager2 viewPager2, MusicManager musicManager) {
        this.mContext = context;
        mDiffer = new AsyncListDiffer<Play>(this, mItemCallback);
        mMusicManager = musicManager;
        this.mViewPager2 = viewPager2;
        this.mViewPager2.setPageTransformer(new PageTransformer());
        //this.mViewPager2.setOffscreenPageLimit(2);
        mOnPageChangeCallback = new OnPageChangeCallback();
        this.mViewPager2.registerOnPageChangeCallback(mOnPageChangeCallback);
        this.mViewPager2.setAdapter(this);
    }

    private class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (isManualScroll) {
                mOnPageChangeListener.onPageSelected(position);
            } else {
                isManualScroll = true;
            }
        }
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        isManualScroll = false;
        mViewPager2.setCurrentItem(position, smoothScroll);
        isManualScroll = true;
    }

    public void setIsManualScroll(boolean isManualScroll) {
        this.isManualScroll = isManualScroll;
    }

    public void setList(ArrayList<Play> list) {
        isManualScroll = false;
        mDiffer.submitList(list);
    }

    public List<Play> getList() {
        return mDiffer.getCurrentList();
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_viewpager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.iv_AlbumArt.setImageBitmap(MusicMetadataRetriever.getInstance().getAlbumArt(mMusicManager.getMusics().get(getList().get(position).getPosition()).getPath()));
    }

    @Override
    public int getItemCount() {
        return getList() == null ? 0 : getList().size();
    }

    private DiffUtil.ItemCallback<Play> mItemCallback = new DiffUtil.ItemCallback<Play>() {
        @Override
        public boolean areItemsTheSame(@NonNull Play oldItem, @NonNull Play newItem) {
            return oldItem.getPosition() == newItem.getPosition();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Play oldItem, @NonNull Play newItem) {
            return oldItem.getPosition() == newItem.getPosition();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_AlbumArt;

        public ViewHolder(View view) {
            super(view);
            iv_AlbumArt = view.findViewById(R.id.adapter_viewpager_album_art);
        }
    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);
    }
}
