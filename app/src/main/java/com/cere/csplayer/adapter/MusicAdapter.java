package com.cere.csplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cere.csplayer.R;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.until.Utils;
import com.cere.csplayer.view.RecyclerViewDivider;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CheRevir on 2019/10/24
 */
public class MusicAdapter extends FastScrollRecyclerView.Adapter<MusicAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter, RecyclerViewListener.OnItemListener {
    private Context mContext;
    private FastScrollRecyclerView mRecyclerView;
    private AsyncListDiffer<Music> mDiffer;
    private int tip = -1, lastTip = 0;
    private static final int TIP = 0;
    private OnItemClickListener mOnItemClickListener;
    private onItemLongClickListener mOnItemLongClickListener;

    public MusicAdapter(Context context, FastScrollRecyclerView recyclerView) {
        this.mContext = context;
        mDiffer = new AsyncListDiffer<Music>(this, mMusicItemCallback);
        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addOnItemTouchListener(new RecyclerViewListener(mRecyclerView, this));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(Color.parseColor("#00000000"), 20));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(this);
    }

    public void setTip(int position) {
        this.tip = position;
        this.notifyItemChanged(lastTip, TIP);
        this.notifyItemChanged(tip, TIP);
        lastTip = position;
    }

    public void smoothScrollToTip() {
        mRecyclerView.smoothScrollToPosition(tip);
    }

    public void scrollToTip() {
        mRecyclerView.scrollToPosition(tip);
    }

    public void setList(ArrayList<Music> list) {
        mDiffer.submitList(list);
    }

    public List<Music> getList() {
        return mDiffer.getCurrentList();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(onItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if ((int) payloads.get(0) == TIP && holder.tip != null) {
                if (this.tip == position) {
                    holder.tip.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                } else {
                    holder.tip.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.tip != null) {
            if (this.tip == position) {
                holder.tip.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            } else {
                holder.tip.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            }
        }
        holder.tv_title.setText(getList().get(position).getTitle());
        holder.tv_artist.setText(getList().get(position).getArtist());
        holder.tv_duration.setText(Utils.timeToString(getList().get(position).getDuration()));
        holder.tv_number.setText(position + 1 + "/" + getList().size());
    }

    @Override
    public int getItemCount() {
        return getList() == null ? 0 : getList().size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return getList().get(position).getTitle().substring(0, 1);
    }

    @Override
    public void onItemClick(int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position);
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(position);
        }
    }

    private DiffUtil.ItemCallback<Music> mMusicItemCallback = new DiffUtil.ItemCallback<Music>() {
        @Override
        public boolean areItemsTheSame(@NonNull Music oldItem, @NonNull Music newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Music oldItem, @NonNull Music newItem) {
            return oldItem.getPath().equals(newItem.getPath());
        }
    };

    class ViewHolder extends FastScrollRecyclerView.ViewHolder {
        private View tip;
        private TextView tv_title, tv_artist, tv_duration, tv_number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tip = itemView.findViewById(R.id.adapter_music_tip);
            tv_title = itemView.findViewById(R.id.adapter_music_title);
            tv_artist = itemView.findViewById(R.id.adapter_music_artist);
            tv_duration = itemView.findViewById(R.id.adapter_music_duration);
            tv_number = itemView.findViewById(R.id.adapter_music_number);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface onItemLongClickListener {
        void onItemLongClick(int position);
    }
}
