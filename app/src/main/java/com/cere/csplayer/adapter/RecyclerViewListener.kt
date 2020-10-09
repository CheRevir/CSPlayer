package com.cere.csplayer.adapter

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by CheRevir on 2020/9/16
 */
class RecyclerViewListener(
    private val recyclerView: RecyclerView,
    private val listener: OnRecyclerItemListener
) :
    GestureDetector.SimpleOnGestureListener(), RecyclerView.OnItemTouchListener {
    private var detector: GestureDetectorCompat = GestureDetectorCompat(recyclerView.context, this)

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return e?.let {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            view?.let { v ->
                listener.onItemClick(recyclerView.getChildAdapterPosition(v))
                true
            } ?: false
        } ?: false
    }

    override fun onLongPress(e: MotionEvent?) {
        super.onLongPress(e)
        e?.let {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            view?.let { v ->
                listener.onItemLongClick(recyclerView.getChildAdapterPosition(v))
            }
        }
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        detector.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    interface OnRecyclerItemListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }
}