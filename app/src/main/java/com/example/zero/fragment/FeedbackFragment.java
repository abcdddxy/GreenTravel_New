package com.example.zero.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;

/**
 * Created by jojo on 2017/10/16.
 */

public class FeedbackFragment extends Fragment {

    private View feedbackFrag;
    private TextView textView;
    private Context context;
    private FrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        feedbackFrag = inflater.inflate(R.layout.fragment_feedback, container, false);
        context = feedbackFrag.getContext();
        innitView();
        MainApplication application = (MainApplication) getActivity().getApplication();
        if (!application.isOnline()) {
            frameLayout.addView(textView);
        } else {
            frameLayout.removeView(textView);
        }
        return feedbackFrag;
    }

    public void innitView() {
        frameLayout = (FrameLayout) feedbackFrag.findViewById(R.id.feedback_fl);
        textView = new TextView(context);
        textView.setText("您还没有任何反馈");
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setCompoundDrawablePadding(30);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.no_feedback, 0, 0);
        textView.setPadding(0, 500, 0, 0);
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.gray1, null));
    }
}