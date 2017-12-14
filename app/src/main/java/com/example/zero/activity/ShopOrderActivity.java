package com.example.zero.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zero.adapter.ShopCartAdapter;
import com.example.zero.bean.ShopCartBean;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopOrderActivity extends AppCompatActivity {

    private TextView tvShopCartSubmit, tvShopCartSelect, tvShopCartTotalNum, tvShopCartHint;
    private View mEmtryView;

    private RecyclerView rlvShopCart, rlvHotProducts;
    private ShopCartAdapter mShopCartAdapter;
    private LinearLayout llPay;
    private RelativeLayout rlHaveProduct;
    private List<ShopCartBean.CartlistBean> mAllOrderList = new ArrayList<>();
    private ArrayList<ShopCartBean.CartlistBean> mGoPayList = new ArrayList<>();
    private List<String> mHotProductsList = new ArrayList<>();
    private TextView tvShopCartTotalPrice;
    private int mCount, mPosition;
    private float mTotalPrice1;
    private boolean mSelect;

    //前后端接口
    private String shopName;
    private String shopId;
    private String sellerId;
    private int count;
    private int size = 100;
    private String[] idList = new String[size];
    private String[] nameList = new String[size];
    private String[] posterList = new String[size];
    private double[] priceList = new double[size];
    private int[] numList = new int[size];

    private static final String TAG = "ShopOrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order);
        Intent intent = getIntent();
        Bundle mBundle = intent.getExtras();

        tvShopCartSelect = (TextView) findViewById(R.id.tv_shopcart_addselect);
        tvShopCartTotalPrice = (TextView) findViewById(R.id.tv_shopcart_totalprice);
        tvShopCartTotalNum = (TextView) findViewById(R.id.tv_shopcart_totalnum);
        tvShopCartHint = (TextView) findViewById(R.id.tv_shopcart_hint);

        rlHaveProduct = (RelativeLayout) findViewById(R.id.rl_shopcart_have);
        rlvShopCart = (RecyclerView) findViewById(R.id.rlv_shopcart);
        mEmtryView = (View) findViewById(R.id.emtryview);
        mEmtryView.setVisibility(View.GONE);
        llPay = (LinearLayout) findViewById(R.id.ll_pay);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        llPay.setLayoutParams(lp);

        tvShopCartSubmit = (TextView) findViewById(R.id.tv_shopcart_submit);

        rlvShopCart.setLayoutManager(new LinearLayoutManager(this));
        mShopCartAdapter = new ShopCartAdapter(this, mAllOrderList);
        rlvShopCart.setAdapter(mShopCartAdapter);

        //删除商品接口
        mShopCartAdapter.setOnDeleteClickListener(new ShopCartAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position, int cartid) {
                mShopCartAdapter.notifyDataSetChanged();
            }
        });

        //修改数量接口
        mShopCartAdapter.setOnEditClickListener(new ShopCartAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position, int cartid, int count) {
                mCount = count;
                mPosition = position;
            }
        });

        //实时监控全选按钮
        mShopCartAdapter.setResfreshListener(new ShopCartAdapter.OnResfreshListener() {
            @Override
            public void onResfresh(boolean isSelect) {
                mSelect = isSelect;
                if (isSelect) {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                } else {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                }
                float mTotalPrice = 0;
                int mTotalNum = 0;
                mTotalPrice1 = 0;
                mGoPayList.clear();
                for (int i = 0; i < mAllOrderList.size(); i++) {
                    if (mAllOrderList.get(i).getIsSelect()) {
                        mTotalPrice += mAllOrderList.get(i).getPrice() * mAllOrderList.get(i).getCount();
                        mTotalNum += 1;
                        mGoPayList.add(mAllOrderList.get(i));
                    }
                }
                mTotalPrice1 = mTotalPrice;
                tvShopCartTotalPrice.setText("总价：" + mTotalPrice);
                tvShopCartTotalNum.setText("共" + mTotalNum + "件商品");
            }

            @Override
            public void onEmpty() {
                mSelect = false;
                Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);
                tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                float mTotalPrice = 0;
                int mTotalNum = 0;
                mGoPayList.clear();
                mTotalPrice1 = mTotalPrice;
                tvShopCartTotalPrice.setText("总价：" + mTotalPrice);
                tvShopCartTotalNum.setText("共" + mTotalNum + "件商品");
                tvShopCartHint.setVisibility(View.VISIBLE);
            }
        });

        //全选
        tvShopCartSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelect = !mSelect;
                if (mSelect) {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                    for (int i = 0; i < mAllOrderList.size(); i++) {
                        mAllOrderList.get(i).setSelect(true);
                        mAllOrderList.get(i).setShopSelect(true);
                    }
                } else {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);
                    tvShopCartSelect.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                    for (int i = 0; i < mAllOrderList.size(); i++) {
                        mAllOrderList.get(i).setSelect(false);
                        mAllOrderList.get(i).setShopSelect(false);
                    }
                }
                mShopCartAdapter.notifyDataSetChanged();

            }
        });

        shopName = mBundle.getString("shopName");
        shopId = mBundle.getString("shopId");
        sellerId = mBundle.getString("sellerId");
        count = mBundle.getInt("size");
        idList = mBundle.getStringArray("idList");

        nameList = mBundle.getStringArray("nameList");
        posterList = mBundle.getStringArray("posterList");
        priceList = mBundle.getDoubleArray("priceList");
        numList = mBundle.getIntArray("numList");

        initData();
        mShopCartAdapter.notifyDataSetChanged();

        tvShopCartSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                MainApplication application = (MainApplication) getApplication();
                JSONArray orderGoods = new JSONArray();
                for (int i = 0; i < mGoPayList.size(); i++) {
                    JSONObject good = new JSONObject();
                    good.put("bought_num", mGoPayList.get(i).getCount());
                    good.put("goods_name", mGoPayList.get(i).getProductName());
                    good.put("id", mGoPayList.get(i).getProductId());
                    good.put("picture_url", mGoPayList.get(i).getDefaultPic());
                    good.put("price", mGoPayList.get(i).getPrice());
                    good.put("seller_id", sellerId);
                    orderGoods.add(good);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", application.getUser_id());
                params.put("seller_id", sellerId);
                RequestManager.getInstance(getBaseContext()).requestPostByAsynBody("order/clearing", params, orderGoods.toString(), new RequestManager.ReqCallBack<String>() {
                    @Override
                    public void onReqSuccess(String result) {
                        JSONObject extraMessage = JSON.parseObject(result);
                        JSONArray invalid = JSON.parseArray(extraMessage.getString("invalid_goods_id"));
                        int count = 0;
                        for (int i = 0; i < mGoPayList.size(); i++) {
                            if (!invalid.contains(mGoPayList.get(i).getProductId())) count++;
                        }
                        if (count > 0) {
                            Bundle mBundle = new Bundle();
                            Intent intent = new Intent(getBaseContext(), OrderConfirmActivity.class);
                            String[] idList = new String[count];
                            String[] nameList = new String[count];
                            String[] posterList = new String[count];
                            double[] priceList = new double[count];
                            int[] numList = new int[count];
                            for (int i = 0; i < mGoPayList.size(); i++) {
                                if (invalid.contains(idList[i])) continue;
                                idList[i] = mGoPayList.get(i).getProductId();
                                nameList[i] = mGoPayList.get(i).getProductName();
                                posterList[i] = mGoPayList.get(i).getDefaultPic();
                                priceList[i] = mGoPayList.get(i).getPrice();
                                numList[i] = mGoPayList.get(i).getCount();
                            }
                            mBundle.putString("shopName", shopName);
                            mBundle.putString("shopId", shopId);
                            mBundle.putString("sellerId", sellerId);
                            mBundle.putInt("size", count);
                            mBundle.putStringArray("idList", idList);
                            mBundle.putStringArray("nameList", nameList);
                            mBundle.putStringArray("posterList", posterList);
                            mBundle.putDoubleArray("priceList", priceList);
                            mBundle.putIntArray("numList", numList);
                            mBundle.putString("coupons", extraMessage.getString("coupons"));
                            mBundle.putString("mailing", extraMessage.getString("mailing_infos"));
                            intent.putExtras(mBundle);
                            startActivity(intent);
                            if (count < mGoPayList.size())
                                Toast.makeText(getBaseContext(), "您所选的部分商品已失效", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "您所选的商品已下架", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Toast.makeText(getBaseContext(), "失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMsg);
                    }
                });


            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initData() {
        for (int i = 0; i < count; i++) {
            ShopCartBean.CartlistBean sb = new ShopCartBean.CartlistBean();
            sb.setShopId(shopId);
            sb.setShopName(shopName);
            sb.setPrice(priceList[i]);
            sb.setDefaultPic(posterList[i]);
            sb.setProductName(nameList[i]);
            sb.setProductId(idList[i]);
            sb.setCount(numList[i]);
            // TODO: 2017/11/14 暂时未定义属性
            sb.setColor("NULL");
            sb.setSize("NULL");
            mAllOrderList.add(sb);
        }
        isSelectFirst(mAllOrderList);
    }

    public static void isSelectFirst(List<ShopCartBean.CartlistBean> list) {
        if (list.size() > 0) {
            list.get(0).setIsFirst(1);
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).getShopId() == list.get(i - 1).getShopId()) {
                    list.get(i).setIsFirst(2);
                } else {
                    list.get(i).setIsFirst(1);
                }
            }
        }

    }
}
