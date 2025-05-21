package com.sky.exp3.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sky.exp3.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ✅ 获取 SharedPreferences
        SharedPreferences sp = getSharedPreferences("userInfor", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
//            Log.d("DailyActivity","dfsdfsdfds" + isLoggedIn);
            // ✅ 已登录，直接跳转主界面
            startActivity(new Intent(MainActivity.this, DailyActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 适配边缘到边缘的显示效果
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取控件对象
        EditText usernameEdit = findViewById(R.id.user_name);      // 用户名输入框
        EditText passwordEdit = findViewById(R.id.user_pwd);       // 密码输入框
        Button loginBtn = findViewById(R.id.login_btn);            // 登录按钮
        Button siginBtn = findViewById(R.id.sigin_btn);            // 注册按钮
        CheckBox savepwd = findViewById(R.id.save_pwd);            // “记住密码”复选框


        // 检查是否启用了“记住密码”，如果是，则回填用户名和密码
        boolean isRemember = sp.getBoolean("rememberPassword", false);
        if (isRemember) {
            String savedUser = sp.getString("username", "");
            String savedPwd = sp.getString("password", "");
            usernameEdit.setText(savedUser);
            passwordEdit.setText(savedPwd);
            savepwd.setChecked(true);
        }

        // 登录按钮点击事件
        loginBtn.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();

            // 获取保存的用户名和密码
            String savedUser = sp.getString("username", "");
            String savedPwd = sp.getString("password", "");

            // 校验用户名和密码是否匹配
            if (savedUser.equals(username) && savedPwd.equals(password)) {
                // 登录成功
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                // 判断用户是否勾选了“记住密码”
                SharedPreferences.Editor editor = getSharedPreferences("userInfor", MODE_PRIVATE).edit();
                if (savepwd.isChecked()) {
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("rememberPassword", true);
                    editor.putBoolean("is_logged_in", true); // ✅ 新增：保存登录状态
                } else {
                    editor.clear(); // 没勾选则清除之前保存的内容
                }
                editor.apply(); // 提交修改

                // 跳转到主界面（每日记录页面）
                Intent intent = new Intent(MainActivity.this, DailyActivity.class);
                startActivity(intent);
                finish();  // 关闭登录页，防止返回到登录界面
            } else {
                // 登录失败
                Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 注册按钮点击事件：跳转到注册页面
        siginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Signin.class);
            startActivity(intent);
        });
    }
}
