package com.mad.sharpdesign.editmenu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import org.w3c.dom.Text;

/**
 * Fragment class for a Strength Fragment
 */
public class StrengthFragment extends Fragment {
    private SeekBar mStrengthSeekbar;
    private TextView mStrengthTextView;
    private Button mApplyButton;
    private int mMaxStrength, mCurrentStrength;
    private static final String STRENGTH_KEY = "Strength";

    /**
     * Instantiator to create a fragment with args, in this case, the strength.
     * @param strength
     * @return
     */
    public static StrengthFragment newInstance(int strength) {
        StrengthFragment strengthFragment = new StrengthFragment();
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
        mStrengthTextView.setText("0");
        mStrengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCurrentStrength = i;
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
