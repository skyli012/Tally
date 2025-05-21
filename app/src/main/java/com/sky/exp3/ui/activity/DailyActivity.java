package com.sky.exp3.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sky.exp3.R;
import com.sky.exp3.ui.adapter.TallyAdapter;
import com.sky.exp3.ui.fragment.DateRangeBottomSheetFragment;
import com.sky.exp3.ui.fragment.TallyBottomSheetFragment;
import com.sky.exp3.data.db.TallyDbHelper;
import com.sky.exp3.data.model.TallyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 每日记账界面，负责显示账单列表并支持增删改查操作。
 */
public class DailyActivity extends AppCompatActivity implements TallyBottomSheetFragment.OnDataSavedListener {

    private RecyclerView recyclerView;        // 显示账单的RecyclerView
    private TextView tvEmpty;                 // 无账单时显示的提示文字
    private TallyAdapter adapter;             // 自定义适配器用于绑定账单数据
    private TallyDbHelper dbHelper;           // 数据库帮助类

    private TallyItem editingItem = null;     // 当前正在编辑的账单项（可选）

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView totalExpense, totalIncome, netAmount;

    private View summaryLayout;


    public void setEditingItem(TallyItem item) {
        this.editingItem = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 设置沉浸式状态栏
        DailyActivity.super.setContentView(R.layout.activity_daily);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        summaryLayout = findViewById(R.id.summaryLayout);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            loadData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // 设置窗口边距以适配状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化控件
        AppCompatButton tallyButton = findViewById(R.id.tally); // “记一笔”按钮
        recyclerView = findViewById(R.id.billRecyclerView);     // 列表视图
        tvEmpty = findViewById(R.id.tvEmpty);                   // 空列表提示文字

        // 统计相关的三个TextView
        totalExpense = findViewById(R.id.totalExpense);
        totalIncome = findViewById(R.id.totalIncome);
//        netAmount = findViewById(R.id.netAmount);

        // 初始化数据库
        dbHelper = new TallyDbHelper(this);

        // 设置RecyclerView布局和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TallyAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 设置“记一笔”按钮点击事件，弹出底部表单
        tallyButton.setOnClickListener(v -> {
            TallyBottomSheetFragment bottomSheet = new TallyBottomSheetFragment();
            bottomSheet.setOnDataSavedListener(this); // 设置数据保存监听
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        // 删除监听器
        adapter.setOnDeleteListener(position -> {
            TallyItem item = adapter.getItem(position);
            boolean deleted = dbHelper.deleteDataById(item.getId());
            if (deleted) {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                loadData(); // 删除成功后刷新列表
            } else {
                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 编辑监听器
        adapter.setOnEditListener(position -> {
            TallyItem item = adapter.getItem(position);
            TallyBottomSheetFragment fragment = new TallyBottomSheetFragment();
            fragment.setEditMode(true);          // 设置为编辑模式
            fragment.setEditItem(item);          // 设置要编辑的对象
            fragment.setOnDataSavedListener(this); // 设置保存监听（刷新列表）
            fragment.show(getSupportFragmentManager(), "TallyBottomSheetFragment");
        });

        ImageButton filterButton = findViewById(R.id.btn_edit);
        filterButton.setOnClickListener(v -> {
            DateRangeBottomSheetFragment dateRangeFragment = new DateRangeBottomSheetFragment();
            dateRangeFragment.setOnDateRangeSelectedListener((start, end) -> {
                loadFilteredData(start, end); // 这里进行查询
            });
            dateRangeFragment.show(getSupportFragmentManager(), "DateRangeBottomSheet");
        });


        // 加载数据
        loadData();
    }

    /**
     * 计算总支出、总收入、净支出并显示在对应控件上
     */
    private void calculateAndDisplaySummary(List<TallyItem> tallyList) {
        double totalExpenseAmount = 0;
        double totalIncomeAmount = 0;

        for (TallyItem item : tallyList) {
            totalExpenseAmount += item.getAmount();
//            Log.d("DailyActivity", "id=" + item.getId() + ", type=" + item.getType() + ", amount=" + item.getAmount());
//            if ("收入".equals(item.getType())) {
//                totalIncomeAmount += item.getAmount();
//            } else if ("支出".equals(item.getType())) {
//                totalExpenseAmount += item.getAmount();
//            }
        }


        double netAmountValue = totalIncomeAmount - totalExpenseAmount;
        Log.d("DailyActivity", "收入: " + totalIncomeAmount + " 支出: " + totalExpenseAmount + " 净额: " + netAmountValue);
        totalExpense.setText(String.format("总支出：¥%.2f", totalExpenseAmount));
        totalIncome.setText(String.format("总收入：¥%.2f", totalIncomeAmount));
//        netAmount.setText(String.format("净支出：¥%.2f", netAmountValue));
    }

    /**
     * 当底部表单保存数据后调用，刷新账单列表
     */
    @Override
    public void onDataSaved() {
        runOnUiThread(this::loadData); // 回到主线程刷新列表
    }

    /**
     * 从数据库加载账单数据并更新UI
     */
    private void loadData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TallyDbHelper.TABLE_NAME,       // 表名
                null,                           // 所有列
                null, null, null, null,
                TallyDbHelper.COLUMN_TIME + " DESC" // 按时间倒序排列
        );

        List<TallyItem> tallyList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_AMOUNT));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_TIME));
            tallyList.add(new TallyItem(id, type, amount, time));
        }


        cursor.close();
        db.close();

        if (tallyList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            summaryLayout.setVisibility(View.GONE); // 没有记录时隐藏
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            summaryLayout.setVisibility(View.VISIBLE); // 有记录时显示
        }

        if (tallyList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter.setData(tallyList);

        // 计算并显示统计数据
        calculateAndDisplaySummary(tallyList);
    }

    private void loadFilteredData(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = TallyDbHelper.COLUMN_TIME + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};

        Cursor cursor = db.query(
                TallyDbHelper.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null,
                TallyDbHelper.COLUMN_TIME + " DESC"
        );

        List<TallyItem> filteredList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_AMOUNT));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(TallyDbHelper.COLUMN_TIME));
            filteredList.add(new TallyItem(id, type, amount, time));
        }

        cursor.close();
        db.close();

        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            summaryLayout.setVisibility(View.GONE); // 没有记录时隐藏
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            summaryLayout.setVisibility(View.VISIBLE); // 有记录时显示
        }


        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter.setData(filteredList);

        // 计算并显示统计数据
        calculateAndDisplaySummary(filteredList);
    }

    /**
     * 当用户从其他界面返回此界面时，自动刷新数据
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
