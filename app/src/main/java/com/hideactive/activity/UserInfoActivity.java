package com.hideactive.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hideactive.R;
import com.hideactive.config.Constant;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.dialog.EditTextDialog;
import com.hideactive.dialog.SelectSexDialog;
import com.hideactive.fragment.BaseFragment;
import com.hideactive.model.User;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_IMAGE_NATIVE = 0;

    private CircleImageView userLogoView;
    private TextView userNameView;
    private TextView userSexView;
    private TextView userAgeView;

    private View userLogoItemView;
    private View userNameItemView;
    private View userSexItemView;
    private View userAgeItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
    }

    public void initView() {
        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.user_info));
        Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
        topBarLeftBtn.setVisibility(View.VISIBLE);
        topBarLeftBtn.setText(getResources().getString(R.string.back));
        topBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        userLogoView = (CircleImageView) findViewById(R.id.user_logo);
        userNameView = (TextView) findViewById(R.id.user_name);
        userSexView = (TextView) findViewById(R.id.user_sex);
        userAgeView = (TextView) findViewById(R.id.user_age);

        User user = application.getCurrentUser();
        if (user.getLogo() != null) {
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
        } else {
            userLogoView.setImageResource(R.mipmap.user_logo_default);
        }
        userNameView.setText(user.getNickname());
        userSexView.setText(user.getSex().intValue() == 0 ? "男" : "女");
        userAgeView.setText(user.getAge().toString());

        userLogoItemView = findViewById(R.id.user_item_logo);
        userNameItemView = findViewById(R.id.user_item_name);
        userSexItemView = findViewById(R.id.user_item_sex);
        userAgeItemView = findViewById(R.id.user_item_age);

        userLogoItemView.setOnClickListener(this);
        userNameItemView.setOnClickListener(this);
        userSexItemView.setOnClickListener(this);
        userAgeItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_item_logo:
                selectImageFromLocal();
                break;
            case R.id.user_item_name:
                EditTextDialog editNameDialog = new EditTextDialog(this,
                        "昵称", "起个绚丽的名字吧！", InputType.TYPE_CLASS_TEXT, application.getCurrentUser().getNickname(),
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setNickname(contentStr);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        userNameView.setText(contentStr);
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        ToastUtil.showShort("更新用户信息失败:" + s);
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });
                editNameDialog.show();
                break;
            case R.id.user_item_sex:
                SelectSexDialog selectSexDialog = new SelectSexDialog(this,
                        application.getCurrentUser().getSex(), new SelectSexDialog.OnDoneListener(){
                            @Override
                            public void onDone(final Integer sex) {
                                loadingDialog.show();
                                User user = new User();
                                user.setSex(sex);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        userSexView.setText(sex.intValue() == 0 ? "男" : "女");
                                        loadingDialog.dismiss();
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        ToastUtil.showShort("更新用户信息失败:" + s);
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });
                selectSexDialog.show();
                break;
            case R.id.user_item_age:
                EditTextDialog editAgeDialog = new EditTextDialog(this,
                        "年龄", "你还是18岁吗？", InputType.TYPE_CLASS_NUMBER,
                        String.valueOf(application.getCurrentUser().getAge()),
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setAge(Integer.parseInt(contentStr));
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        userAgeView.setText(contentStr);
                                        loadingDialog.dismiss();
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        ToastUtil.showShort("更新用户信息失败:" + s);
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });
                editAgeDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 上传用户头像
     * @param imagePath
     */
    private void uploadLogo(String imagePath, final Bitmap bitmap) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        loadingDialog.show();
        BmobProFile.getInstance(this).upload(imagePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                User user = new User();
                user.setLogo(bmobFile);
                updateUser(user, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        userLogoView.setImageBitmap(bitmap);
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ToastUtil.showShort("更新用户信息失败:" + s);
                        loadingDialog.dismiss();
                    }
                });
            }
            @Override
            public void onProgress(int i) {
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }

    /**
     * 更新user信息
     * @param newUser
     * @param listener
     */
    private void updateUser(User newUser, UpdateListener listener) {
        if (newUser == null) {
            return;
        }
        User currentUser = application.getCurrentUser();
        newUser.update(this, currentUser.getObjectId(), listener);
    }

    /**
     * 获取本地图片
     */
    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_IMAGE_NATIVE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE_NATIVE && data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    Cursor cursor = this.getContentResolver().query(
                            selectedImage, null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex("_data");
                    String localSelectPath = cursor.getString(columnIndex);
                    cursor.close();
                    if (localSelectPath == null || localSelectPath.equals("null")) {
                        ToastUtil.showShort("未取到图片！");
                        return;
                    }
                    Bitmap nativeBitmap = null;
                    File localFile = new File(localSelectPath);
                    // 若此文件小于100KB，直接使用。为了减轻缓存容量
                    if (localFile.length() < 102400) {
                        nativeBitmap = BitmapFactory.decodeFile(localSelectPath);
                    } else {
                        String nativePath = Constant.IMAGE_CACHE_PATH  + File.separator + String.valueOf(System.currentTimeMillis());
                        nativeBitmap = PhotoUtil.compressImage(localSelectPath, nativePath, false);
                        localSelectPath = nativePath;
                    }
                    uploadLogo(localSelectPath, nativeBitmap);
                }
            }
        }
    }
}