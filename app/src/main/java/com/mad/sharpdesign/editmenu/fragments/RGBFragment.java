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
import com.mad.sharpdesign.events.RGBEvent;
import com.mad.sharpdesign.events.StrengthEvent;

import org.greenrobot.eventbus.EventBus;

public class RGBFragment extends Fragment {
    private SeekBar mRedSeekBar, mGreenSeekBar, mBlueSeekBar;
    private TextView mRedTextView, mGreenTextView, mBlueTextView;
    private Button mApplyButton;
    private int mCurrentRed, mCurrentGreen, mCurrentBlue, mMaxRed, mMinRed, mMaxGreen, mMinGreen, mMaxBlue, mMinBlue, mProgress;
    private static final String RED_KEY = "Red";
    private static final String GREEN_KEY = "Green";
    private static final String BLUE_KEY = "Blue";
    private static final String PROGRESS_KEY = "Progress";


    public static RGBFragment newInstance(int red, int green, int blue, int progress) {
        RGBFragment rgbFragment = new RGBFragment();
        Bundle args = new Bundle();
        args.putInt(RED_KEY, red);
        args.putInt(GREEN_KEY, green);
        args.putInt(BLUE_KEY, blue);
        args.putInt(PROGRESS_KEY, progress);

        rgbFragment.setArguments(args);
        return rgbFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMaxRed = getArguments().getInt(RED_KEY);
        mMaxGreen = getArguments().getInt(GREEN_KEY);
        mMaxBlue = getArguments().getInt(BLUE_KEY);
        mProgress = getArguments().getInt(PROGRESS_KEY);

        return inflater.inflate(R.layout.fragment_rgb, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRedSeekBar = (SeekBar)getView().findViewById(R.id.fragment_rgb_redSeekBar);
        mGreenSeekBar = (SeekBar)getView().findViewById(R.id.fragment_rgb_greenSeekBar);
        mBlueSeekBar = (SeekBar)getView().findViewById(R.id.fragment_rgb_blueSeekBar);
        mRedTextView = (TextView)getView().findViewById(R.id.fragment_rgb_redTextView);
        mGreenTextView = (TextView)getView().findViewById(R.id.fragment_rgb_greenTextView);
        mBlueTextView = (TextView)getView().findViewById(R.id.fragment_rgb_blueTextView);
        mApplyButton = (Button)getView().findViewById(R.id.fragment_rgb_applyButton);
        mRedSeekBar.setMax(mMaxRed);
        mRedSeekBar.setProgress(mMaxRed / 2);
        mGreenSeekBar.setMax(mMaxGreen);
        mGreenSeekBar.setProgress(mMaxGreen / 2);
        mBlueSeekBar.setMax(mMaxBlue);
        mBlueSeekBar.setProgress(mMaxBlue / 2);

        mCurrentRed = mCurrentBlue = mCurrentGreen = 1;
        mRedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCurrentRed = i - (mMaxRed / 2);
                mRedTextView.setText(Integer.toString(mCurrentRed));
                updateRGB();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mGreenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCurrentGreen = i - (mMaxGreen / 2);
                mGreenTextView.setText(Integer.toString(mCurrentGreen));
                updateRGB();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mBlueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCurrentBlue = i - (mMaxBlue /2 );
                mBlueTextView.setText(Integer.toString(mCurrentBlue));
                updateRGB();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ApplyEvent(true));
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateRGB() {
        EventBus.getDefault().post(new RGBEvent(mCurrentRed, mCurrentGreen, mCurrentBlue));
    }
}
