package com.cere.csplayer.adapter

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

/**
 * Created by CheRevir on 2019/10/24
 */
class RecyclerViewListener(recyclerView: RecyclerView, onItemListener: OnItemListener) : OnItemTouchListener {
    private val mGestureDetectorCompat: GestureDetectorCompat

    init {
        mGestureDetectorCompat = GestureDetectorCompat(recyclerView.context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    onItemListener.onItemClick(recyclerView.getChildLayoutPosition(childView))
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    onItemListener.onItemLongClick(recyclerView.getChildLayoutPosition(childView))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        mGestureDetectorCompat.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    interface OnItemListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }
}