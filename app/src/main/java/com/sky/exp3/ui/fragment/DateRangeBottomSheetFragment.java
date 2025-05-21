package com.sky.exp3.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sky.exp3.R;

import java.util.Calendar;
import java.util.Locale;

public class DateRangeBottomSheetFragment extends BottomSheetDialogFragment {

    private TextView tvStartDate, tvEndDate;
    private Button btnQuery;
    private String startDate = null;
    private String endDate = null;

    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(String startDate, String endDate);
    }

    private OnDateRangeSelectedListener listener;

    public void setOnDateRangeSelectedListener(OnDateRangeSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_bottom_sheet, container, false);

        tvStartDate = view.findViewById(R.id.tv_start_date);
        tvEndDate = view.findViewById(R.id.tv_end_date);
        btnQuery = view.findViewById(R.id.btn_query);

        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));

        btnQuery.setOnClickListener(v -> {
            if (startDate == null || endDate == null) {
                Toast.makeText(getContext(), "请选择完整的日期范围", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) {
                listener.onDateRangeSelected(startDate, endDate);
            }
            dismiss(); // 关闭弹窗
        });

        return view;
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (isStart) {
                startDate = date;
                tvStartDate.setText(date);
            } else {
                endDate = date;
                tvEndDate.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
