package com.sky.exp3.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sky.exp3.R;
import com.sky.exp3.data.model.TallyItem;

import java.util.List;

/**
 * RecyclerView 适配器，用于展示账单数据列表（TallyItem）
 */
public class TallyAdapter extends RecyclerView.Adapter<TallyAdapter.ViewHolder> {

    private List<TallyItem> mData;
    private OnDeleteListener deleteListener;
    private OnEditListener editListener;

    public TallyAdapter(List<TallyItem> data) {
        this.mData = data;
    }

    /**
     * 获取某个位置的账单项
     */
    public TallyItem getItem(int position) {
        return mData != null ? mData.get(position) : null;
    }

    /**
     * 设置删除事件监听器
     */
    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }

    /**
     * 设置编辑事件监听器
     */
    public void setOnEditListener(OnEditListener listener) {
        this.editListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tally, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TallyItem item = mData.get(position);
        if (item == null) return;

        holder.tvType.setText(item.getType());
        holder.tvAmount.setText(String.format("¥%.2f", item.getAmount()));
        holder.tvTime.setText(item.getTime());

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(position);
            }
        });

        // 编辑按钮点击事件
        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 更新数据并刷新列表
     */
    public void setData(List<TallyItem> data) {
        this.mData = data;
        // Debug 输出每个账单的 ID（可选）
        for (TallyItem item : data) {
            System.out.println("TallyItem id = " + item.getId());
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder 类，绑定 item 布局控件
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvTime;
        ImageButton btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }

    /**
     * 删除按钮点击接口
     */
    public interface OnDeleteListener {
        void onDelete(int position);
    }

    /**
     * 编辑按钮点击接口
     */
    public interface OnEditListener {
        void onEdit(int position);
    }
}
