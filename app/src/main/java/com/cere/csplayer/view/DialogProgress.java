package com.cere.csplayer.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.widget.ContentLoadingProgressBar;

import com.cere.csplayer.R;

/**
 * Created by CheRevir on 2019/10/22
 */
public class DialogProgress extends AlertDialog {
    private ContentLoadingProgressBar mProgressBar;
    private TextView mTextView;

    public DialogProgress(Context context) {
        super(context);
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_progress, null);
        mProgressBar = layout.findViewById(R.id.dialog_progress_progress);
        mTextView = layout.findViewById(R.id.dialog_progress_message);
    }

    public void setProgressBarColor(@ColorInt int color) {
        mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void setMessage(CharSequence message) {
        mTextView.setText(message);
    }
}
