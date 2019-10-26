package com.cere.csplayer.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by CheRevir on 2019/10/26
 */
public class BaseFragment extends Fragment {

    public static BaseFragment newInstance() {
        Bundle bundle = new Bundle();
        BaseFragment fragment = new BaseFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
