package com.mad.sharpdesign.editmenu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mad.sharpdesign.R;
import com.mad.sharpdesign.events.ApplyEvent;
import com.mad.sharpdesign.events.StrengthEvent;

import org.greenrobot.eventbus.EventBus;

public class BlankFragment extends Fragment {

    private Button mApplyButton;


    public static BlankFragment newInstance() {
        BlankFragment blankFragment = new BlankFragment();
        Bundle args = new Bundle();
        blankFragment.setArguments(args);
        return blankFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mApplyButton = (Button)getView().findViewById(R.id.fragment_blank_applyButton);
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ApplyEvent(true));
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
