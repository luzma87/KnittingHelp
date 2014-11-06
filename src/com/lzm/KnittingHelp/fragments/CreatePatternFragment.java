package com.lzm.KnittingHelp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;

/**
 * Created by DELL on 05/11/2014.
 */
public class CreatePatternFragment extends MasterFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.create_pattern_layout, container, false);

        return view;
    }
}