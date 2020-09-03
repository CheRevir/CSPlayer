package com.cere.csplayer.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.PorterDuff
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.widget.ContentLoadingProgressBar
import com.cere.csplayer.R

/**
 * Created by CheRevir on 2019/10/22
 */
@SuppressLint("InflateParams")
class DialogProgress(context: Context) : AlertDialog(context) {
    private val mProgressBar: ContentLoadingProgressBar
    private val mTextView: TextView

    init {
        val layout = layoutInflater.inflate(R.layout.dialog_progress, null) as LinearLayout
        mProgressBar = layout.findViewById(R.id.dialog_progress_progress)
        mTextView = layout.findViewById(R.id.dialog_progress_message)
    }

    @Suppress("DEPRECATION")
    fun setProgressBarColor(@ColorInt color: Int) {
        mProgressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    override fun setMessage(message: CharSequence) {
        mTextView.text = message
    }
}