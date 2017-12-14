package com.example.zero.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.zero.greentravel_new.R;
import com.example.zero.util.MainApplication;
import com.example.zero.util.RequestManager;
import com.example.zero.view.SimpleTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * Created by jojo on 2017/9/27.
 */

//TODO 用户数据修改写回

public class UserActivity extends Activity implements View.OnClickListener {

    private TextView backArrow;
    private LinearLayout user_img;
    private RoundedImageView mImage;
    private View changeName;
    private View changePhone;
    private View changePw;
    private Bitmap mBitmap;
    private TextView user_name, user_phone;
    private Button signOut;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;
    private static final String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        innitView();
        signOut.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        user_img.setOnClickListener(this);
        changeName.setOnClickListener(this);
        changePhone.setOnClickListener(this);
        changePw.setOnClickListener(this);
    }

    public void innitView() {
        user_name = (TextView) findViewById(R.id.user_name_gray);
        backArrow = (TextView) findViewById(R.id.user_back_arrow);
        user_img = (LinearLayout) findViewById(R.id.user_img_change);
        mImage = (RoundedImageView) findViewById(R.id.user_img_show);
        changeName = (View) findViewById(R.id.user_name_change);
        changePhone = (View) findViewById(R.id.user_phone_change);
        changePw = (View) findViewById(R.id.user_pw_change);
        signOut = (Button) findViewById(R.id.sign_out);
        user_phone = (TextView) findViewById(R.id.user_phone);
        MainApplication application = (MainApplication) getApplication();
        user_name.setText(application.getUsername());
        String s = application.getPhone();
        if (s != null) {
            s = s.substring(0, 3) + "****" + s.substring(9, 11);
            user_phone.setText(s);
        }

        String avator = application.getAvator();
        if (avator != null && !avator.equals("")) {
            avator = RequestManager.getInstance(getBaseContext()).getBaseUrl() + "/users/" + avator + "?type=0";
            Glide.with(getContext())
                    .load(avator)
                    .dontAnimate()
                    .placeholder(R.drawable.defult_user_img)
                    .into(mImage);
        }
    }

    /**
     * 显示修改用户名的对话框
     */
    protected void showChangeNameDialog() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_change_name, null);
        final SimpleTextView newName = view.findViewById(R.id.dialog_name);
        newName.setHintText("新用户名");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改用户名")
                .setView(view)
                .setPositiveButton("确定", null)
                .setNegativeButton("关闭", null);
        final AlertDialog dialog = builder.show();
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
                params.put("user_id", sharedPreferences.getString("user_id", ""));
                params.put("user_name", newName.getText().trim());
                RequestManager.getInstance(getBaseContext()).requestAsyn("users/update_user_info", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                    @Override
                    public void onReqSuccess(String result) {
                        Log.d(TAG, result);
                        Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT);
                        user_name.setText(newName.getText().trim());
                        MainApplication mainApplication = (MainApplication) getApplication();
                        mainApplication.setUsername(newName.getText().trim());
                        dialog.dismiss();
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Toast.makeText(getBaseContext(), "修改失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, errorMsg);
                    }
                });
            }
        });
    }

    /**
     * 显示修改密码的对话框
     */
    protected void showChangePwDialog() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_change_pw, null);
        final SimpleTextView prePw = view.findViewById(R.id.dialog_pw_pre);
        final SimpleTextView newPw = view.findViewById(R.id.dialog_pw_new);
        final SimpleTextView confirmPw = view.findViewById(R.id.dialog_pw_confirm);
        prePw.setHintText("原密码");
        newPw.setHintText("新密码");
        confirmPw.setHintText("重复新密码");
        prePw.setLeftImage(R.drawable.lock_fill);
        newPw.setLeftImage(R.drawable.lock_fill);
        confirmPw.setLeftImage(R.drawable.lock_fill);
        prePw.setPw();
        newPw.setPw();
        confirmPw.setPw();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(view)
                .setPositiveButton("确定", null)
                .setNegativeButton("关闭", null);
        final AlertDialog dialog = builder.show();
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (confirmPw.getText().trim().equals(newPw.getText().trim())) {
                    HashMap<String, String> params = new HashMap<>();
                    SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
                    params.put("user_id", sharedPreferences.getString("user_id", ""));
                    params.put("old_psw", prePw.getText().trim());
                    params.put("new_psw", newPw.getText().trim());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/update_psw", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT);
                            dialog.dismiss();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "修改失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * 显示修改手机的对话框
     */
    protected void showChangePhoneDialog() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_change_phone, null);
        final SimpleTextView newPhone = view.findViewById(R.id.dialog_phone_new);
        final SimpleTextView confirm = view.findViewById(R.id.dialog_phone_pre_confirm);
        final TextView sendConfirm = view.findViewById(R.id.dialog_phone_pre_button);
        newPhone.setHintText("新手机号");
        confirm.setHintText("新手机收到的验证码");
        newPhone.setLeftImage(R.drawable.user_fill);
        confirm.setLeftImage(R.drawable.identify);
        sendConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = newPhone.getText().toString().trim();
                if (RegisterActivity.isMobile(text)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("type", "2");
                    params.put("phone", newPhone.getText());
                    MainApplication application = (MainApplication) getApplication();
                    params.put("user_id", application.getUser_id());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/send_verification_code", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            Toast.makeText(getBaseContext(), "已发送短信", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改绑定手机")
                .setView(view)
                .setPositiveButton("确定", null)
                .setNegativeButton("关闭", null);
        final AlertDialog dialog = builder.show();
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newString = newPhone.getText().toString().trim();
                if (RegisterActivity.isMobile(newString)) {
                    HashMap<String, String> params = new HashMap<>();
                    SharedPreferences sharedPreferences = getSharedPreferences("GreenTravel", MODE_PRIVATE);
                    params.put("user_id", sharedPreferences.getString("user_id", ""));
                    params.put("new_phone", newPhone.getText().trim());
                    params.put("verification_code", confirm.getText().trim());
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/update_phone", RequestManager.TYPE_POST_JSON, params, new RequestManager.ReqCallBack<String>() {
                        @Override
                        public void onReqSuccess(String result) {
                            Log.d(TAG, result);
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT);
                            String s = newPhone.getText().trim();
                            MainApplication mainApplication = (MainApplication) getApplication();
                            mainApplication.setPhone(s);
                            s = s.substring(0, 3) + "****" + s.substring(9, 11);
                            user_phone.setText(s);
                            dialog.dismiss();
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "修改失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "请输入正确的手机号" + newString, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 显示修改图片的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("添加图片");
        String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent();
                        openAlbumIntent.setAction(Intent.ACTION_PICK);
                        //Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE:
                        // 拍照
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp_image.jpg"));
                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上,并上传服务器
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            //这里图片是方形的，可以用一个工具类处理成圆形
            mImage.setImageBitmap(mBitmap);//显示图片
            //在这个地方可以写上上传该图片到服务器的代码
            File extDir = getContext().getFilesDir();
            String filename = "temp.jpg";
            File file = new File(extDir, filename);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getLocalizedMessage());
            }

            final HashMap<String, Object> params = new HashMap<>();
            final MainApplication application = (MainApplication) getApplication();
            params.put("user_id", application.getUser_id());
            params.put("file", file);

            RequestManager.getInstance(getBaseContext()).upLoadFile("users/upload_photo", params, new RequestManager.ReqCallBack<String>() {

                @Override
                public void onReqSuccess(String result) {

                    Log.d(TAG, result.toString());
                    JSONObject object = JSON.parseObject(result);
                    String url = object.getString("avator_url");

                    final HashMap<String, String> params2 = new HashMap<>();
                    params2.put("user_id", application.getUser_id());
                    params2.put("photo_url", url);
                    application.setAvator(url);
                    RequestManager.getInstance(getBaseContext()).requestAsyn("users/update_user_info", RequestManager.TYPE_POST_JSON, params2, new RequestManager.ReqCallBack<String>() {

                        @Override
                        public void onReqSuccess(String result) {
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, result);
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(getBaseContext(), "修改失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, errorMsg);
                        }
                    });

                }

                @Override
                public void onReqFailed(String errorMsg) {
                    Toast.makeText(getBaseContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, errorMsg);
                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.user_img_change:
                showChoosePicDialog();
                break;
            case R.id.user_back_arrow:
                finish();
                break;
            case R.id.user_name_change:
                showChangeNameDialog();
                break;
            case R.id.user_phone_change:
                showChangePhoneDialog();
                break;
            case R.id.user_pw_change:
                showChangePwDialog();
                break;
            case R.id.sign_out:
                MainApplication application = (MainApplication) getApplication();
                application.logout();
                finish();
                break;
            default:
                break;
        }
    }
}
