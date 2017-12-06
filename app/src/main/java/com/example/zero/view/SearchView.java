package com.example.zero.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zero.greentravel_new.R;

public class SearchView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框
     */
    private EditText etInput;
    /**
     * 删除键
     */
    private ImageView ivDelete;
    /**
     * 弹出列表
     */
    private ListView lvTips;
    /**
     * 上下文对象
     */
    private Context mContext;
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
    private SearchViewListener mListener;

    private static final String TAG = "SearchView";

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    public SearchView(Context context, AttributeSet attrs) {
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
        lvTips = (ListView) findViewById(R.id.route_lv_list);

        lvTips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //设置提示文本
                String text = lvTips.getAdapter().getItem(i).toString();
                Log.d(TAG, "onItemClick: " + i + " " + text);
                if (mListener.onHintClick(text)) {
                    etInput.setText(text);
                    etInput.setSelection(text.length());
                    //提示列表
                    lvTips.setVisibility(View.GONE);
                    notifyStartSearching(text);
                } else {
                    Toast.makeText(getContext(), "站点在数据库中不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivDelete.setOnClickListener(this);
        //btnBack.setOnClickListener(this);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = etInput.getText().toString();
                    lvTips.setVisibility(GONE);
                    if (mListener.onHintClick(text)) {
                        notifyStartSearching(text);
                    } else {
                        etInput.setText("");
                        Toast.makeText(getContext(), "站点在数据库中不存在", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
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
        if (lvTips.getAdapter() == null) {
            lvTips.setAdapter(mHintAdapter);
        }
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
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
                lvTips.setVisibility(VISIBLE);
                if (mAutoCompleteAdapter != null && lvTips.getAdapter() != mAutoCompleteAdapter) {
                    lvTips.setAdapter(mAutoCompleteAdapter);
                }
                //更新autoComplete数据
                if (mListener != null) {
                    mListener.onRefreshAutoComplete(charSequence + "");
                }
            } else {
                ivDelete.setVisibility(GONE);
                if (mHintAdapter != null) {
                    lvTips.setAdapter(mHintAdapter);
                }
                lvTips.setVisibility(GONE);
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
                lvTips.setVisibility(VISIBLE);
                mListener.isFocus();
                break;
            case R.id.route_iv_delete:
                etInput.setText("");
                ivDelete.setVisibility(GONE);
                mListener.onBack();
                break;
            default:
                break;
        }
    }

    /**
     * 返回输入框文本内容
     *
     * @return 搜索栏文本
     */
    public String getText() {
        return etInput.getText().toString();
    }

    /**
     * 返回是否有焦点
     *
     * @return true有焦点
     */
    public boolean isFocus() {
        return etInput.hasFocus();
    }

    /**
     * 返回提示框可见性
     *
     * @return true可见
     */
    public int getLvTipsVisible() {
        return lvTips.getVisibility();
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {

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