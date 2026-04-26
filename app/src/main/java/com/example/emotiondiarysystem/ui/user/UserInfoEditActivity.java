package com.example.emotiondiarysystem.ui.user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.User;
import com.example.emotiondiarysystem.manager.UserManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SessionUtil;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInfoEditActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageButton btnBack;
    private Button btnSave;
    private ImageView ivAvatar;
    private LinearLayout btnChangeAvatar;
    private RelativeLayout avatarContainer;
    private TextView tvAvatarHint;
    private EditText etNickname;
    private TextView tvAccount;
    private LinearLayout topBar;
    private View divider;

    private UserManager userManager;
    private User currentUser;
    private String currentAvatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initManagers();
        setListeners();
        loadUserInfo();
        applyFullTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        ivAvatar = findViewById(R.id.iv_avatar);
        btnChangeAvatar = findViewById(R.id.btn_change_avatar);
        avatarContainer = findViewById(R.id.avatar_container);
        tvAvatarHint = findViewById(R.id.tv_avatar_hint);
        etNickname = findViewById(R.id.et_nickname);
        tvAccount = findViewById(R.id.tv_account);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
    }

    private void initManagers() {
        userManager = new UserManager(this);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnChangeAvatar.setOnClickListener(this);
        if (avatarContainer != null) avatarContainer.setOnClickListener(this);
        if (ivAvatar != null) ivAvatar.setOnClickListener(this);
        if (tvAvatarHint != null) tvAvatarHint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        } else if (v.getId() == R.id.btn_save) {
            saveUserInfo();
        } else if (v.getId() == R.id.btn_change_avatar
                || v.getId() == R.id.avatar_container
                || v.getId() == R.id.iv_avatar
                || v.getId() == R.id.tv_avatar_hint) {
            showAvatarPicker();
        }
    }

    private void loadUserInfo() {
        currentUser = resolveCurrentUser();
        if (currentUser != null) {
            etNickname.setText(currentUser.getNickname() != null ? currentUser.getNickname() : "");
            tvAccount.setText(currentUser.getAccount());
            currentAvatarPath = currentUser.getAvatar();
            loadAvatar();
        } else {
            // 尽量给页面兜底展示，避免用户看到空白
            String account = getIntent().getStringExtra("account");
            if (TextUtils.isEmpty(account)) {
                account = SpUtil.getString(this, "account", "");
            }
            String nickname = getIntent().getStringExtra("nickname");
            if (TextUtils.isEmpty(nickname)) {
                nickname = SpUtil.getString(this, "nickname", "");
            }
            if (!TextUtils.isEmpty(account)) {
                tvAccount.setText(account);
            }
            if (!TextUtils.isEmpty(nickname)) {
                etNickname.setText(nickname);
            }
            loadAvatar();
        }
    }

    private User resolveCurrentUser() {
        int fallbackUserId = SessionUtil.ensureUserId(this);
        int userId = getIntent().getIntExtra("userId", fallbackUserId);
        if (userId > 0) {
            User user = userManager.getUserById(userId);
            if (user != null) {
                return user;
            }
        }

        String account = getIntent().getStringExtra("account");
        if (TextUtils.isEmpty(account)) {
            account = SpUtil.getString(this, "account", "");
        }
        if (!TextUtils.isEmpty(account)) {
            return userManager.login(account);
        }
        return null;
    }

    private void loadAvatar() {
        if (currentAvatarPath != null && !currentAvatarPath.isEmpty()) {
            Glide.with(this).load(currentAvatarPath).into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    private void showAvatarPicker() {
        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setItems(new String[]{"从相册选择", "拍照"}, (dialog, which) -> {
                    if (which == 0) {
                        pickFromGallery();
                    } else {
                        if (checkCameraPermission()) {
                            takePhoto();
                        } else {
                            requestCameraPermission();
                        }
                    }
                })
                .show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                ToastUtil.showShort(this, "需要相机权限才能拍照");
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentAvatarPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        tryPersistUriPermission(data, uri);
                        String localPath = copyImageToLocalStorage(uri);
                        if (!TextUtils.isEmpty(localPath)) {
                            currentAvatarPath = localPath;
                            loadAvatar();
                        } else {
                            ToastUtil.showShort(this, "头像处理失败，请重试");
                        }
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                loadAvatar();
            }
        }
    }

    private void tryPersistUriPermission(Intent data, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && data != null) {
            int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                getContentResolver().takePersistableUriPermission(uri, flags);
            } catch (SecurityException ignored) {
            }
        }
    }

    private String copyImageToLocalStorage(Uri uri) {
        File avatarDir = new File(getFilesDir(), "avatar");
        if (!avatarDir.exists() && !avatarDir.mkdirs()) {
            return null;
        }

        String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
        File targetFile = new File(avatarDir, fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            if (inputStream == null) {
                return null;
            }
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private void saveUserInfo() {
        if (currentUser == null) {
            currentUser = resolveCurrentUser();
        }
        if (currentUser == null) {
            ToastUtil.showShort(this, "用户信息加载失败，请重新登录");
            return;
        }

        String inputNickname = etNickname.getText() != null ? etNickname.getText().toString().trim() : "";
        String nicknameToSave = TextUtils.isEmpty(inputNickname)
                ? (currentUser.getNickname() == null ? "" : currentUser.getNickname())
                : inputNickname;

        // 兼容“只改昵称”或“只改头像”：任一字段存在更新即可保存
        String avatarToSave = TextUtils.isEmpty(currentAvatarPath) ? null : currentAvatarPath;

        boolean success = userManager.updateUserInfo(currentUser.getUserId(), nicknameToSave, avatarToSave);
        if (success) {
            currentUser.setNickname(nicknameToSave);
            currentUser.setAvatar(avatarToSave);
            SpUtil.putString(this, "nickname", nicknameToSave);
            ToastUtil.showShort(this, "保存成功");
            finish();
        } else {
            ToastUtil.showShort(this, "保存失败，请重试");
        }
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);
        if (divider != null) divider.setBackgroundColor(colors.divider);

        if (btnBack != null) btnBack.setColorFilter(colors.iconTint);

        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) tvTitle.setTextColor(colors.textPrimary);

        if (btnSave != null) {
            btnSave.setTextColor(colors.primary);
        }

        if (etNickname != null) {
            etNickname.setTextColor(colors.textPrimary);
            etNickname.setHintTextColor(colors.textSecondary);
        }

        if (tvAccount != null) {
            tvAccount.setTextColor(colors.textSecondary);
        }

        if (btnChangeAvatar != null) {
            Drawable background = btnChangeAvatar.getBackground();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(colors.primary);
            } else if (background instanceof ColorDrawable) {
                btnChangeAvatar.setBackgroundColor(colors.primary);
            }
        }

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }
}