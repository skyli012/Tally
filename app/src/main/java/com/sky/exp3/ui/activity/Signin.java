package com.sky.exp3.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sky.exp3.R;

public class Signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        // 适配系统窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 获取 UI 组件
        EditText usernameEdit = findViewById(R.id.sign_username);
        EditText passwordEdit = findViewById(R.id.sign_passward);
        EditText repasswordEdit = findViewById(R.id.sign_repassward);
        Button registerBtn = findViewById(R.id.register_btn);
        Button cancelBtn = findViewById(R.id.cancel_btn);

        // 2. 注册按钮点击事件
        registerBtn.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            String repassword = repasswordEdit.getText().toString().trim();

            // 表单验证
            if (username.isEmpty()) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (repassword.isEmpty()) {
                Toast.makeText(this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(repassword)) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            // 保存注册信息到 SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("userInfor", MODE_PRIVATE).edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.apply();

            Toast.makeText(this, "注册成功，请返回登录", Toast.LENGTH_SHORT).show();

            // 结束当前活动，返回上一个界面
            finish();
        });

        // 3. 取消按钮返回主界面
        cancelBtn.setOnClickListener(v -> {
            startActivity(new Intent(Signin.this, MainActivity.class));
        });
    }
}
