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

/**
 * Fragment class for a seekbar that starts at 0, and goes into negatives.
 */
public class NegativeStrengthFragment extends Fragment {
    private SeekBar mStrengthSeekbar;
    private TextView mStrengthTextView;
    private Button mApplyButton;
    private int mMaxStrength, mCurrentStrength;
    private static final String STRENGTH_KEY = "Strength";

    /**
     * Instantiator for the fragment that accepts an argument, in this case, the strength
     * @param strength
     * @return
     */
    public static NegativeStrengthFragment newInstance(int strength) {
        NegativeStrengthFragment strengthFragment = new NegativeStrengthFragment();
        Bundle args = new Bundle();
        args.putInt(STRENGTH_KEY, strength);
        strengthFragment.setArguments(args);
        return strengthFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMaxStrength = getArguments().getInt(STRENGTH_KEY);

        return inflater.inflate(R.layout.fragment_strength, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mStrengthSeekbar = (SeekBar)getView().findViewById(R.id.fragment_strength_seekbar);
        mStrengthTextView = (TextView)getView().findViewById(R.id.fragment_strength_textView);
        mApplyButton = (Button)getView().findViewById(R.id.fragment_strength_applyButton);
        mStrengthSeekbar.setMax(mMaxStrength);
        mStrengthSeekbar.setProgress(mMaxStrength / 2);
        mStrengthTextView.setText(Integer.toString(mStrengthSeekbar.getProgress()));
        mStrengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCurrentStrength = i - mMaxStrength / 2;
                mStrengthTextView.setText(Integer.toString(i));
                EventBus.getDefault().post(new StrengthEvent(mCurrentStrength));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //Apply a test of the image manip here

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
}
