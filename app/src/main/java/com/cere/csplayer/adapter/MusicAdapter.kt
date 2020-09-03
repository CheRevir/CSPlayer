package com.cere.csplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cere.csplayer.R
import com.cere.csplayer.adapter.RecyclerViewListener.OnItemListener
import com.cere.csplayer.entity.Music
import com.cere.csplayer.util.Utils.timeToString
import com.cere.csplayer.view.RecyclerViewDivider
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView.SectionedAdapter

/**
 * Created by CheRevir on 2019/10/24
 */
class MusicAdapter(private val mContext: Context, recyclerView: FastScrollRecyclerView) : RecyclerView.Adapter<MusicAdapter.ViewHolder>(), SectionedAdapter, OnItemListener {
    companion object {
        private const val TIP = 0
    }

    private val mRecyclerView: FastScrollRecyclerView
    val list: List<Music>?
        get() = mDiffer.currentList
    private val mDiffer: AsyncListDiffer<Music>
    private var tip = -1
    private var lastTip = 0
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    private val mMusicItemCallback: DiffUtil.ItemCallback<Music> = object : DiffUtil.ItemCallback<Music>() {
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.path == newItem.path
        }
    }

    init {
        mDiffer = AsyncListDiffer(this, mMusicItemCallback)
        mRecyclerView = recyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.addOnItemTouchListener(RecyclerViewListener(mRecyclerView, this))
        mRecyclerView.addItemDecoration(RecyclerViewDivider(Color.parseColor("#00000000"), 20))
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.adapter = this
    }

    fun setTip(position: Int) {
        tip = position
        this.notifyItemChanged(lastTip, TIP)
        this.notifyItemChanged(tip, TIP)
        lastTip = position
    }

    fun smoothScrollToTip() {
        if (tip > 0)
            mRecyclerView.smoothScrollToPosition(tip)
    }

    fun scrollToTip() {
        if (tip > 0)
            mRecyclerView.scrollToPosition(tip)
    }

    fun setList(list: ArrayList<Music>) {
        mDiffer.submitList(list)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(OnItemLongClickListener: OnItemLongClickListener) {
        mOnItemLongClickListener = OnItemLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_music, parent, false))
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (payloads[0] as Int == TIP) {
                if (tip == position) {
                    holder.tip.setBackgroundColor(mContext.resources.getColor(R.color.colorAccent))
                } else {
                    holder.tip.setBackgroundColor(mContext.resources.getColor(R.color.transparent))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (tip == position) {
            holder.tip.setBackgroundColor(mContext.resources.getColor(R.color.colorAccent))
        } else {
            holder.tip.setBackgroundColor(mContext.resources.getColor(R.color.transparent))
        }
        holder.tvTitle.text = list!![position].title
        holder.tvArtist.text = list!![position].artist
        holder.tvDuration.text = timeToString(list!![position].duration.toLong())
        holder.tvNumber.text = "${position + 1}/${list!!.size}"
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getSectionName(position: Int): String {
        return list!![position].title!!.substring(0, 1)
    }

    override fun onItemClick(position: Int) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(position)
        }
    }

    override fun onItemLongClick(position: Int) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener!!.onItemLongClick(position)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tip: View = itemView.findViewById(R.id.adapter_music_tip)
        val tvTitle: TextView = itemView.findViewById(R.id.adapter_music_title)
        val tvArtist: TextView = itemView.findViewById(R.id.adapter_music_artist)
        val tvDuration: TextView = itemView.findViewById(R.id.adapter_music_duration)
        val tvNumber: TextView = itemView.findViewById(R.id.adapter_music_number)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}