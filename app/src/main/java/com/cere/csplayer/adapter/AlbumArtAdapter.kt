package com.cere.csplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.request.RequestOptions
import com.cere.csplayer.GlideApp
import com.cere.csplayer.R
import com.cere.csplayer.content.MusicManager
import com.cere.csplayer.entity.Play
import com.cere.csplayer.util.FileUtils
import com.cere.csplayer.view.PageTransformer
import java.util.*

/**
 * Created by CheRevir on 2019/10/21
 */
@Suppress("DEPRECATION")
@SuppressLint("CheckResult")
class AlbumArtAdapter(private val mContext: Context, viewPager2: ViewPager2, musicManager: MusicManager) : RecyclerView.Adapter<AlbumArtAdapter.ViewHolder>() {
    private val mViewPager2: ViewPager2
    private val mDiffer: AsyncListDiffer<Play>
    private val mMusicManager: MusicManager
    val list: List<Play>?
        get() = mDiffer.currentList
    private val mOnPageChangeCallback: OnPageChangeCallback
    private var mOnPageChangeListener: OnPageChangeListener? = null
    private var isManualScroll = false
    private val mRequestOptions: RequestOptions = RequestOptions()
    private var position = 0

    private val mItemCallback: DiffUtil.ItemCallback<Play> = object : DiffUtil.ItemCallback<Play>() {
        override fun areItemsTheSame(oldItem: Play, newItem: Play): Boolean {
            return oldItem.position == newItem.position
        }

        override fun areContentsTheSame(oldItem: Play, newItem: Play): Boolean {
            return oldItem.position == newItem.position
        }
    }

    init {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        canvas.drawRect(0f, 0f, 1f, 1f, paint)
        mRequestOptions.error(BitmapDrawable(bitmap))
        mDiffer = AsyncListDiffer(this, mItemCallback)
        mMusicManager = musicManager
        mViewPager2 = viewPager2
        mViewPager2.setPageTransformer(PageTransformer())
        mOnPageChangeCallback = OnPageChangeCallback()
        mViewPager2.registerOnPageChangeCallback(mOnPageChangeCallback)
        mViewPager2.adapter = this
    }

    private inner class OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (isManualScroll) {
                mOnPageChangeListener!!.onPageSelected(position)
            } else {
                isManualScroll = true
            }
        }
    }

    fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        isManualScroll = false
        this.position = position
        mViewPager2.setCurrentItem(position, smoothScroll)
        isManualScroll = true
    }

    fun setIsManualScroll(isManualScroll: Boolean) {
        this.isManualScroll = isManualScroll
    }

    fun setList(list: ArrayList<Play>) {
        isManualScroll = false
        mDiffer.submitList(list)
    }

    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_viewpager, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = mMusicManager.musics!![list!![position].position].path
        if (FileUtils.isExists(path!!)) {
            GlideApp.with(mContext).asBitmap().apply(mRequestOptions).load(path).into(holder.ivAlbumart)
        }
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAlbumart: ImageView = view.findViewById(R.id.adapter_viewpager_album_art)

    }

    interface OnPageChangeListener {
        fun onPageSelected(position: Int)
    }
}