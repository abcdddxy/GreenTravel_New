package com.example.zero.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.example.zero.fragment.HelpFeedbackFragmentController;
import com.example.zero.greentravel_new.R;

/**
 * Created by jojo on 2017/10/10.
 */

public class HelpFeedbackActivity extends AppCompatActivity {
    private ImageView backArrow;
    private RadioGroup radioGroup;
    private HelpFeedbackFragmentController controller;
    private View help, feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);
        innitView();
        controller = HelpFeedbackFragmentController.getInstance(this, R.id.help_feedback_content);
        controller.showFragment(0);
        /**
         *  监听器
         */
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.help:
                        controller.showFragment(0);
                        help.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, null));
                        feedback.setBackgroundColor(getResources().getColor(R.color.white, null));
                        break;
                    case R.id.feedback:
                        controller.showFragment(1);
                        feedback.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, null));
                        help.setBackgroundColor(getResources().getColor(R.color.white, null));
                    default:
                        break;
                }
            }
        });
    }

    public void innitView() {
        backArrow = (ImageView) findViewById(R.id.help_feedback_back_arrow);
        radioGroup = (RadioGroup) findViewById(R.id.help_feedback_rg);
        help = (View) findViewById(R.id.help_line);
        feedback = (View) findViewById(R.id.feedback_line);
    }
}
