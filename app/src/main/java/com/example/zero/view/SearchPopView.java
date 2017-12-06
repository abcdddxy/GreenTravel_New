package com.example.zero.view;

/**
 * Created by kazu_0122 on 2017/9/15.
 * 带下拉弹窗的搜索框
 */

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.example.zero.greentravel_new.R;

public class SearchPopView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框
     */
    private EditText etInput;
    /**
     * 删除键
     */
    private ImageView ivDelete;
    /**
     * 上下文对象
     */
    private Context mContext;
    /**
     * 弹出列表
     */
    private ListPopupWindow popupWindow;
    /**
     * 提示adapter （推荐adapter）
     */
    private ArrayAdapter<String> mHintAdapter;
    /**
     * 自动补全adapter 只显示名字
     */
    private ArrayAdapter<String> mAutoCompleteAdapter;
    /**
     * 搜索回调接口
     */
    private SearchPopViewListener mListener;
    /**
     * 是否显示pop
     */
    private boolean JUD;

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchPopViewListener(SearchPopViewListener listener) {
        mListener = listener;
    }

    public SearchPopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.route_search, this);
        initViews();
    }

    public void setHintText(String str) {
        etInput.setHint(str);
    }

    /**
     * 初始化View
     */
    private void initViews() {
        etInput = (EditText) findViewById(R.id.route_et_search);
        ivDelete = (ImageView) findViewById(R.id.route_iv_delete);
        //btnBack = (Button) findViewById(R.id.route_btn_back);

        ivDelete.setOnClickListener(this);
        //btnBack.setOnClickListener(this);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    popupWindow.dismiss();
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });

        JUD = true;

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        etInput.measure(w, h);
        int width = etInput.getMeasuredWidth();
        popupWindow = new ListPopupWindow(getContext());
        popupWindow.setAnchorView(etInput);
        popupWindow.setHeight(400);
        popupWindow.setModal(false);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (etInput.getText().toString().equals("")) {
                    etInput.setText(mHintAdapter.getItem(position));
                } else {
                    etInput.setText(mAutoCompleteAdapter.getItem(position));
                }
                popupWindow.dismiss();
                notifyStartSearching(etInput.getText().toString());
            }
        });

    }

    /**
     * 通知监听者 进行搜索操作
     *
     * @param text 搜索文本
     */
    private void notifyStartSearching(String text) {
        if (mListener != null) {
            mListener.onSearch(etInput.getText().toString());
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置热搜版提示 adapter
     */
    public void setTipsHintAdapter(ArrayAdapter<String> adapter) {
        this.mHintAdapter = adapter;
        popupWindow.setAdapter(adapter);
    }

    public void setJUD(boolean B) {
        JUD = B;
    }

    /**
     * 设置自动补全adapter
     */
    public void setAutoCompleteAdapter(ArrayAdapter<String> adapter) {
        this.mAutoCompleteAdapter = adapter;
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
                popupWindow.setWidth(etInput.getWidth());
                if (mAutoCompleteAdapter != null) {
                    popupWindow.setAdapter(mAutoCompleteAdapter);
                }
                //更新autoComplete数据
                if (mListener != null) {
                    mListener.onRefreshAutoComplete(charSequence + "");
                }
                if ((mAutoCompleteAdapter.getCount() != 0) && JUD) {
//                    Toast.makeText(getContext(), "11111", Toast.LENGTH_SHORT).show();
                    popupWindow.show();
                }
            } else {
                ivDelete.setVisibility(GONE);
                if (mHintAdapter != null) {
                    popupWindow.setAdapter(mHintAdapter);
                }
                popupWindow.dismiss();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.route_et_search:
                mListener.isFocus();
                if (!popupWindow.isShowing() && ((etInput.getText().toString().equals("") && mHintAdapter.getCount() != 0)
                        || ((!etInput.getText().toString().equals("")) && mAutoCompleteAdapter.getCount() != 0)) && JUD) {
//                    Toast.makeText(getContext(), "22222", Toast.LENGTH_SHORT).show();
                    popupWindow.show();
                }
                break;
            case R.id.route_iv_delete:
                etInput.setText("");
                mListener.onBack();
                ivDelete.setVisibility(GONE);
                break;
            default:
                break;
        }

    }

    /**
     * 返回输入框文本内容
     *
     * @return String
     */
    public String getText() {
        return etInput.getText().toString();
    }

    /**
     * 设置输入框文本内容
     *
     * @return String
     */
    public void setText(String str) {
        etInput.setText(str);
    }

    /**
     * 返回是否有焦点
     *
     * @return
     */
    public boolean isFocus() {
        return etInput.hasFocus();
    }

    /**
     * 返回提示框可见性
     *
     * @return
     */
    public boolean getWindowVisible() {
        return popupWindow.isShowing();
    }


    /**
     * search view回调方法
     */
    public interface SearchPopViewListener {

        /**
         * 更新自动补全内容
         *
         * @param text 传入补全后的文本
         */
        void onRefreshAutoComplete(String text);

        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);

        /**
         * 设置返回操作
         */
        void onBack();

        /**
         * 提示框出现
         */
        void isFocus();

        /**
         * 提示框搜索项点击
         *
         * @return true 数据库存在
         */
        boolean onHintClick(String text);
    }

}