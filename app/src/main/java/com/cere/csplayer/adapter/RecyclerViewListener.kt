package com.cere.csplayer.adapter

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by CheRevir on 2020/9/16
 */
class RecyclerViewListener(
    recyclerView: RecyclerView,
    listener: OnRecyclerItemListener
) :
    RecyclerView.OnItemTouchListener {
    private var detector: GestureDetectorCompat

    init {
        detector =
            GestureDetectorCompat(
                recyclerView.context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
                        val view = recyclerView.findChildViewUnder(e!!.x, e.y)
                        view?.let {
                            listener.onItemClick(recyclerView.getChildAdapterPosition(view))
                        }
                        return true
                    }

                    override fun onLongPress(e: MotionEvent?) {
                        super.onLongPress(e)
                        val view = recyclerView.findChildViewUnder(e!!.x, e.y)
                        view?.let {
                            listener.onItemLongClick(recyclerView.getChildAdapterPosition(view))
                        }
                    }
                })
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