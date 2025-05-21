package com.sky.exp3.ui.fragment;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sky.exp3.R;
import com.sky.exp3.data.db.TallyDbHelper;
import com.sky.exp3.data.model.TallyItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 账单添加/编辑弹窗Fragment
 */
public class TallyBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText etAmount, etRemark;
    private RadioGroup rgCategory;
    private Button btnSubmit;
    private TextView tvTitle;

    private boolean isEditMode = false;
    private TallyItem editItem = null;

    // 设置是否为编辑模式
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
    }

    // 设置要编辑的账单条目（编辑时调用）
    public void setEditItem(TallyItem item) {
        this.editItem = item;
    }

    // 回调接口（保存后通知主界面刷新）
    public interface OnDataSavedListener {
        void onDataSaved();
    }

    private OnDataSavedListener listener;

    public void setOnDataSavedListener(OnDataSavedListener listener) {
        this.listener = listener;
    }

    // 通知调用者数据已保存
    private void notifyDataSaved() {
        if (listener != null) {
            listener.onDataSaved();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tally_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 视图绑定
        etAmount = view.findViewById(R.id.et_amount);
        etRemark = view.findViewById(R.id.et_remark);
        rgCategory = view.findViewById(R.id.rg_category);
        btnSubmit = view.findViewById(R.id.btn_submit);
        tvTitle = view.findViewById(R.id.tv_title);

        // 编辑模式下，填充原有数据
        if (isEditMode && editItem != null) {
            tvTitle.setText("编辑账单");
            etAmount.setText(String.valueOf(editItem.getAmount()));
//            etRemark.setText(editItem.getRemark());

            // 设置分类选中项
            for (int i = 0; i < rgCategory.getChildCount(); i++) {
                View child = rgCategory.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (rb.getText().toString().equals(editItem.getType())) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        } else {
            tvTitle.setText("记账");
        }

        // 提交按钮监听
        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "请输入金额", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "金额格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgCategory.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(getContext(), "请选择分类", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedCategory = view.findViewById(checkedId);
            String type = selectedCategory.getText().toString();
            String remark = etRemark.getText().toString().trim();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // 数据库插入或更新
            TallyDbHelper dbHelper = new TallyDbHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(TallyDbHelper.COLUMN_TYPE, type);
            values.put(TallyDbHelper.COLUMN_AMOUNT, amount);
            values.put(TallyDbHelper.COLUMN_TIME, time);
//            values.put(TallyDbHelper.COLUMN_REMARK, remark); // 建议加上备注字段保存

            long result;
            if (editItem == null) {
                // 新增
                result = db.insert(TallyDbHelper.TABLE_NAME, null, values);
            } else {
                // 修改
                String whereClause = TallyDbHelper.COLUMN_ID + "=?";
                String[] whereArgs = {String.valueOf(editItem.getId())};
                result = db.update(TallyDbHelper.TABLE_NAME, values, whereClause, whereArgs);
            }
            db.close();

            if (result != -1) {
                Toast.makeText(getContext(), editItem == null ? "保存成功" : "修改成功", Toast.LENGTH_SHORT).show();
                notifyDataSaved();
                dismiss();
            } else {
                Toast.makeText(getContext(), editItem == null ? "保存失败" : "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置弹窗高度为屏幕一半
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            behavior.setPeekHeight((int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.5));
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
