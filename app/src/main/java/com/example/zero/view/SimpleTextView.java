package com.example.zero.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zero.greentravel_new.R;

/**
 * Created by kazu_0122 on 2017/10/16.
 */

public class SimpleTextView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框
     */
    private EditText etInput;
    /**
     * 删除键
     */
    private ImageView ivDelete;
    /**
     * 返回按钮
     */
    private Context mContext;


    public SimpleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.simple_textview, this);
        initViews();
    }

    public void setText(String str) {
        etInput.setText(str);
    }

    public void setHintText(String str) {
        etInput.setHint(str);
    }

    public void setLeftImage(int id) {
        etInput.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
        etInput.setCompoundDrawablePadding(20);
    }

    public void setPw() {
        etInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        etInput.setOnFocusChangeListener(listener);
    }

    public void notFocused() {
        ivDelete.setVisibility(GONE);
    }

    public void setSelectionEnd(int end) {
        etInput.setSelection(end);
    }

    public void reqFocus() {
        etInput.requestFocus();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        etInput = (EditText) findViewById(R.id.textview_edit);
        ivDelete = (ImageView) findViewById(R.id.textview_cancel);
        ivDelete.setOnClickListener(this);
        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm.isActive()) {

                        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 通知监听者 进行搜索操作
     *
     * @param text 搜索文本
     */
    private void notifyStartSearching(String text) {
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 重写text change类
     */
    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!charSequence.toString().equals("")) {
                ivDelete.setVisibility(VISIBLE);
            } else {
                ivDelete.setVisibility(GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textview_cancel:
                etInput.setText("");
                ivDelete.setVisibility(GONE);
                break;
            default:
                break;
        }

    }

    /**
     * 返回输入框文本内容
     *
     * @return
     */
    public String getText() {
        return etInput.getText().toString();
    }

    public EditText getEtInput() {
        return etInput;
    }

    /**
     * 返回是否有焦点
     *
     * @return
     */
    public boolean isFocus() {
        ivDelete.setVisibility(VISIBLE);
        return etInput.hasFocus();
    }
}